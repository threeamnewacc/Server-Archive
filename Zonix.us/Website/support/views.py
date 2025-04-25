import datetime

from django.http import JsonResponse, Http404
from django.contrib.auth import get_user
from django.contrib.auth.decorators import login_required
from django.shortcuts import render

from authentication.models import Authentication, SiteNotification
from data.models import get_name_from_id
from support.models import Ticket, TicketReply


def support_index(request):
    return render(request, 'support/index.html')


@login_required(redirect_field_name=None)
def tickets_index(request):
    user = get_user(request)

    tickets = []

    for ticket in Ticket.objects.filter(ticket_creator=user.uid).order_by('-updated_date'):
        tickets.append({
            'id': ticket.ticket_id,
            'title': ticket.ticket_title,
            'creator_name': get_name_from_id(ticket.ticket_creator.__str__()),
            'handler_name': None if ticket.ticket_handler is None else get_name_from_id(ticket.ticket_handler.__str__()),
            'type': ticket.ticket_type,
            'type_pretty': ticket.get_pretty_type(),
            'status': ticket.ticket_status,
            'status_pretty': ticket.get_pretty_status(),
            'publish_date': ticket.publish_date.timestamp(),
            'updated_date': ticket.updated_date.timestamp()
        })

    return render(request, 'support/tickets.html', {
        'navigation': 'support',
        'context': {
            'tickets': tickets,
            'tickets_size': 0 if tickets is None else len(tickets),
        }
    })


@login_required(redirect_field_name=None)
def tickets_new(request):
    if request.method == 'POST':
        user = get_user(request)

        if Authentication.is_cooldown(user):
            return JsonResponse({'response': 'cooldown'})

        if 'ticket_subject' not in request.POST or 'ticket_content' not in request.POST or 'ticket_type' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        ticket_subject = request.POST['ticket_subject']
        ticket_content = request.POST['ticket_content']
        ticket_type = request.POST['ticket_type']

        if ticket_subject is None or ticket_type is None or ticket_content is None:
            return JsonResponse({'response': 'missing-fields'})

        tickets = Ticket.objects.filter(ticket_creator=user.uid, ticket_status__in=['a', 'b', 'd', 'e'])

        if tickets is not None and len(tickets) >= 3:
            return JsonResponse({'response': 'limited'})

        ticket = Ticket()
        ticket.ticket_title = ticket_subject
        ticket.ticket_type = ticket_type
        ticket.ticket_content = ticket_content
        ticket.ticket_creator = user.uid
        ticket.ticket_status = 'a'
        ticket.publish_date = datetime.datetime.now()
        ticket.updated_date = datetime.datetime.now()
        ticket.assign_handler()
        ticket.save()

        return JsonResponse({'response': 'success', 'ticket_id': ticket.ticket_id})
    else:
        return render(request, 'support/tickets_new.html', {
            'nav_page': 'support'
        })


@login_required(redirect_field_name=None)
def tickets_view(request, ticket_id):
    try:
        ticket_id = int(ticket_id)
    except ValueError:
        raise Http404()

    user = get_user(request)

    if ticket_id is None:
        raise Http404()
    else:
        ticket = Ticket.objects.filter(ticket_id=ticket_id).first()

        if ticket is None:
            raise Http404()

        if not ticket.can_user_access(user):
            raise Http404()

        replies = []

        for reply in TicketReply.objects.filter(ticket_id=ticket.ticket_id).order_by('publish_date'):
            replies.append({
                'id': reply.reply_id,
                'content': reply.reply_content,
                'creator_name': get_name_from_id(reply.reply_creator.__str__()),
                'publish_date': reply.publish_date.timestamp()
            })

        return render(request, 'support/tickets_view.html', {
            'context': {
                'navigation': 'support',
                'ticket': {
                    'id': ticket.ticket_id,
                    'title': ticket.ticket_title,
                    'content': ticket.ticket_content,
                    'creator_name': get_name_from_id(ticket.ticket_creator.__str__()),
                    'handler_name': None if ticket.ticket_handler is None else get_name_from_id(ticket.ticket_handler.__str__()),
                    'type': ticket.ticket_type,
                    'type_pretty': ticket.get_pretty_type(),
                    'status': ticket.ticket_status,
                    'status_pretty': ticket.get_pretty_status(),
                    'publish_date': ticket.publish_date.timestamp(),
                    'updated_date': ticket.updated_date.timestamp()
                },
                'replies': replies
            }
        })


@login_required(redirect_field_name=None)
def tickets_reply(request):
    if request.method == 'POST':
        if 'ticket_id' not in request.POST or 'reply_content' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        try:
            ticket_id = int(request.POST['ticket_id'])
        except ValueError:
            raise Http404()

        ticket = Ticket.objects.filter(pk=ticket_id).first()

        if ticket is None:
            return JsonResponse({'response': 'missing-record'})

        if ticket.ticket_status == 'c':
            return JsonResponse({'response': 'ticket-closed'})

        user = get_user(request)

        if Authentication.is_cooldown(user):
            return JsonResponse({'response': 'cooldown'})

        if not ticket.can_user_access(user):
            return JsonResponse({'response': 'unauthorized'})

        if ticket.ticket_creator.__str__() == user.uid.__str__():
            ticket.ticket_status = 'd'
        else:
            ticket.ticket_status = 'e'

        ticket_reply = TicketReply()
        ticket_reply.ticket_id = ticket.ticket_id
        ticket_reply.reply_content = request.POST['reply_content']
        ticket_reply.reply_creator = request.user.uid
        ticket_reply.publish_date = datetime.datetime.now()
        ticket_reply.save()

        ticket.updated_date = datetime.datetime.now()
        ticket.save()

        if ticket_reply.reply_creator != ticket.ticket_creator:
            notification = SiteNotification()
            notification.user = ticket.ticket_creator
            notification.type = 'TICKETS_REPLY_TO_TICKET'
            notification.data = {
                'reply_creator': ticket_reply.reply_creator,
                'ticket_id': ticket.ticket_id,
                'ticket_title': ticket.ticket_title
            }
            notification.save()

        return JsonResponse({'response': 'success', 'ticket_reply_id': ticket_reply.reply_id})
    else:
        raise Http404()


@login_required(redirect_field_name=None)
def tickets_close(request, ticket_id):
    if request.method == 'POST':
        try:
            ticket_id = int(ticket_id)
        except ValueError:
            raise Http404()

        user = get_user(request)

        if ticket_id is None:
            return JsonResponse({'response': 'missing-record'})

        ticket = Ticket.objects.filter(pk=ticket_id).first()

        if ticket is None:
            return JsonResponse({'response': 'missing-record'})

        if ticket.ticket_status == 'c':
            return JsonResponse({'response': 'ticket-closed'})

        if not ticket.can_user_access(user):
            return JsonResponse({'response': 'unauthorized'})

        ticket.ticket_status = 'c'
        ticket.updated_date = datetime.datetime.now()
        ticket.save()

        notification = SiteNotification()
        notification.user = ticket.ticket_creator
        notification.type = 'TICKETS_STATUS_CHANGED'
        notification.data = {
            'ticket_id': ticket.ticket_id,
            'ticket_title': ticket.ticket_title
        }
        notification.save()

        return JsonResponse({'response': 'success'})
    else:
        raise Http404()


@login_required(redirect_field_name=None)
def tickets_open(request, ticket_id):
    if request.method == 'POST':
        try:
            ticket_id = int(ticket_id)
        except ValueError:
            raise Http404()

        if ticket_id is None:
            return JsonResponse({'response': 'missing-record'})

        ticket = Ticket.objects.filter(pk=ticket_id).first()

        if ticket is None:
            return JsonResponse({'response': 'missing-record'})

        user = get_user(request)

        if Authentication.is_cooldown(user):
            return JsonResponse({'response': 'cooldown'})

        if not user.is_staff and user.is_admin:
            return JsonResponse({'response': 'unauthorized'})

        ticket.ticket_status = 'a'
        ticket.updated_date = datetime.datetime.now()
        ticket.save()

        notification = SiteNotification()
        notification.user = ticket.ticket_creator
        notification.type = 'TICKETS_STATUS_CHANGED'
        notification.data = {
            'ticket_id': ticket.ticket_id,
            'ticket_title': ticket.ticket_title
        }
        notification.save()

        return JsonResponse({'response': 'success'})
    else:
        raise Http404()


@login_required(redirect_field_name=None)
def tickets_reassign(request, ticket_id):
    if request.method == 'POST':
        try:
            ticket_id = int(ticket_id)
        except ValueError:
            raise Http404()

        if ticket_id is None:
            return JsonResponse({'response': 'missing-record'})

        ticket = Ticket.objects.filter(pk=ticket_id).first()

        if ticket is None:
            return JsonResponse({'response': 'missing-record'})

        if ticket.ticket_status == 'c':
            return JsonResponse({'response': 'ticket-closed'})

        user = get_user(request)

        if Authentication.is_cooldown(user):
            return JsonResponse({'response': 'cooldown'})

        if not user.is_staff and user.is_admin:
            return JsonResponse({'response': 'unauthorized'})

        ticket.assign_handler()
        ticket.save()

        notification = SiteNotification()
        notification.user = ticket.ticket_creator
        notification.type = 'TICKETS_REASSIGNED'
        notification.data = {
            'ticket_id': ticket.ticket_id,
            'ticket_title': ticket.ticket_title
        }
        notification.save()

        return JsonResponse({'response': 'success'})
    else:
        raise Http404()


@login_required(redirect_field_name=None)
def tickets_delete(request, ticket_id):
    try:
        ticket_id = int(ticket_id)
    except ValueError:
        raise Http404()

    user = get_user(request)

    if not (user.uid.__str__() == '77e8431a-dfc5-42ab-98e1-e018e48a734b'):
        return JsonResponse({'response': 'unauthorized'})

    if ticket_id is None:
        return JsonResponse({'response': 'missing-record'})

    ticket = Ticket.objects.filter(pk=ticket_id).first()

    if ticket is None:
        return JsonResponse({'response': 'missing-record'})

    ticket.delete()

    return JsonResponse({'response': 'success'})
