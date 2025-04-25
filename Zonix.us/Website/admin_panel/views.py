from django.contrib.auth import get_user
from django.contrib.auth.models import AnonymousUser
from django.contrib.auth.decorators import login_required
from django.http import Http404, JsonResponse
from django.shortcuts import render

from authentication.models import SiteUser
from data.models import DataHelper, get_name_from_id
from forums.models import Thread, Reply
from support.models import Ticket, TicketBlacklistEntry
from staff_apps.models import StaffApplication


def verify_auth(user):
    if isinstance(user, AnonymousUser):
        return False

    return user.is_authenticated and user.is_staff and user.is_admin


@login_required(redirect_field_name=None)
def admin_index(request):
    user = get_user(request)

    if not verify_auth(user):
        raise Http404()

    users_count = SiteUser.objects.count()
    threads_count = Thread.objects.count()
    replies_count = Reply.objects.count()
    tickets_count = Ticket.objects.count()

    return render(request, 'admin_panel/index.html', {
        'context': {
            'users_count': users_count,
            'threads_count': threads_count,
            'replies_count': replies_count,
            'tickets_count': tickets_count,
            'admin_stats': DataHelper.get_admin_data(),
        }
    })


@login_required(redirect_field_name=None)
def forums_index(request):
    user = get_user(request)

    if not verify_auth(user):
        raise Http404()

    categories = []

    return render(request, 'admin_panel/forums/index.html', {
        'context': {
            'navigation': 'admin_forums',
            'categories': categories,
        }
    })


@login_required(redirect_field_name=None)
def tickets_index(request):
    user = get_user(request)

    if not verify_auth(user):
        raise Http404()

    ticket_blacklist = []

    for entry in TicketBlacklistEntry.objects.all():
        ticket_blacklist.append(get_name_from_id(entry.uid.__str__()))

    return render(request, 'admin_panel/support/index.html', {
        'context': {
            'ticket_blacklist': ticket_blacklist,
            'ticket_blacklist_size': len(ticket_blacklist)
        }
    })


@login_required(redirect_field_name=None)
def ticket_blacklist(request):
    user = get_user(request)

    if not verify_auth(user):
        raise Http404()

    if request.method == 'POST':
        if 'uuid' not in request.POST:
            raise JsonResponse({'response': 'missing-fields'})

        entry = TicketBlacklistEntry.objects.filter(uid=request.POST['uuid']).first()

        if entry is None:
            entry = TicketBlacklistEntry()
            entry.uid = request.POST['uuid']
            entry.save()
        else:
            entry.delete()

        return JsonResponse({'response': 'success'})
    else:
        raise Http404()


@login_required(redirect_field_name=None)
def tickets_all(request, page=1, sort=None):
    user = get_user(request)

    if not verify_auth(user):
        raise Http404()

    try:
        page = int(page)
    except TypeError:
        page = 1

    sorts = ['new', 'old', 'open', 'creator_replied', 'unassigned']

    if sort is None or sort not in sorts:
        objects = Ticket.objects.order_by('-publish_date')
    else:
        if sort == 'new':
            objects = Ticket.objects.order_by('-publish_date')
        elif sort == 'old':
            objects = Ticket.objects.order_by('publish_date')
        elif sort == 'open':
            objects = Ticket.objects.filter(ticket_status='a').order_by('-publish_date')
        elif sort == 'creator_replied':
            objects = Ticket.objects.filter(ticket_status='d').order_by('-publish_date')
        elif sort == 'unassigned':
            objects = Ticket.objects.filter(ticket_handler__isnull=True).order_by('-publish_date')
        else:
            raise Http404()

    pagination = DataHelper.generate_pagination(objects, page, 20)

    tickets = []

    for ticket in pagination['objects']:
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

    return render(request, 'admin_panel/support/ticket_list.html', {
        'context': {
            'tickets': tickets,
            'tickets_size': len(tickets),
            'pagination': pagination,
            'sort': sort
        }
    })


@login_required(redirect_field_name=None)
def tickets_handling(request):
    user = get_user(request)

    if not user.is_staff:
        raise Http404()

    assigned = []

    for ticket in Ticket.objects.filter(ticket_handler=user.uid):
        if ticket.ticket_handler == user.uid and ticket.ticket_status != 'c':
            assigned.append({
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

    return render(request, 'admin_panel/staff/tickets_handling.html', {
        'context': {
            'assigned': assigned,
            'assigned_size': len(assigned),
        }
    })


@login_required(redirect_field_name=None)
def applications_all(request, page=1, sort=None):
    user = get_user(request)

    if not verify_auth(user):
        raise Http404()

    try:
        page = int(page)
    except TypeError:
        page = 1

    sorts = ['new', 'old', 'accepted', 'denied', 'interview', 'review']

    if sort is None or sort not in sorts:
        objects = StaffApplication.objects.order_by('-publish_date')
    else:
        if sort == 'new':
            objects = StaffApplication.objects.order_by('-publish_date')
        elif sort == 'old':
            objects = StaffApplication.objects.order_by('publish_date')
        elif sort == 'review':
            objects = StaffApplication.objects.filter(status='a').order_by('publish_date')
        elif sort == 'accepted':
            objects = StaffApplication.objects.filter(status='c').order_by('publish_date')
        elif sort == 'denied':
            objects = StaffApplication.objects.filter(status='d').order_by('publish_date')
        elif sort == 'interview':
            objects = StaffApplication.objects.filter(status='b').order_by('publish_date')
        else:
            raise Http404()

    pagination = DataHelper.generate_pagination(objects, page, 20)

    applications = []

    for application in pagination['objects']:
        applications.append({
            'id': application.id,
            'creator': get_name_from_id(application.creator.__str__()),
            'status': application.status,
            'status_pretty': application.get_pretty_status(),
            'publish_timestamp': application.publish_date.timestamp(),
            'updated_timestamp': application.updated_date.timestamp()
        })

    return render(request, 'admin_panel/staff_apps/application_list.html', {
        'context': {
            'applications': applications,
            'applications_size': len(applications),
            'pagination': pagination,
            'sort': sort
        }
    })
