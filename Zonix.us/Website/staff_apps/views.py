import datetime

from django.http import JsonResponse, Http404
from django.contrib.auth import get_user
from django.contrib.auth.decorators import login_required
from django.shortcuts import render

from authentication.models import Authentication, SiteNotification
from data.models import get_name_from_id
from staff_apps.models import StaffApplication


@login_required(redirect_field_name=None)
def applications_index(request):
    user = get_user(request)

    applications = []

    for application in StaffApplication.objects.filter(creator=user.uid).order_by('-updated_date'):
        applications.append({
            'id': application.id,
            'creator': get_name_from_id(application.creator.__str__()),
            'status': application.status,
            'status_pretty': application.get_pretty_status(),
            'publish_timestamp': application.publish_date.timestamp(),
            'updated_timestamp': application.updated_date.timestamp()
        })

    return render(request, 'staff_apps/index.html', {
        'navigation': 'staff_apps',
        'context': {
            'applications': applications,
            'applications_size': 0 if applications is None else len(applications),
        }
    })


@login_required(redirect_field_name=None)
def application_new(request):
    user = get_user(request)

    if request.method == 'POST':
        print('request: ' + request.POST.__str__())

        if Authentication.is_cooldown(user):
            return JsonResponse({'response': 'cooldown'})

        fields = ['first_name', 'telegram', 'previous_experience', 'about_yourself', 'cheating_opinion', 'region', 'activity', 'languages']

        for field in fields:
            if field not in request.POST:
                return JsonResponse({'response': 'missing-fields'})

        application = StaffApplication.objects.filter(creator=user.uid, status='a').first()

        if application is not None:
            return JsonResponse({'response': 'limited'})

        application = StaffApplication()
        application.creator = user.uid
        application.publish_date = datetime.datetime.now()
        application.updated_date = datetime.datetime.now()
        application.previous_experience = request.POST['previous_experience']
        application.about_yourself = request.POST['about_yourself']
        application.cheating_opinion = request.POST['cheating_opinion']
        application.region = request.POST['region']
        application.activity = request.POST['activity']
        application.first_name = request.POST['first_name']
        application.telegram = request.POST['telegram']

        for language in ['english', 'spanish', 'german', 'french', 'other']:
            if language in request.POST['languages']:
                if language == 'english':
                    application.languages_english = True
                elif language == 'spanish':
                    application.languages_spanish = True
                elif language == 'german':
                    application.languages_german = True
                elif language == 'french':
                    application.languages_french = True
                elif language == 'other':
                    application.languages_other = True

        application.save()

        return JsonResponse({'response': 'success', 'application_id': application.id})
    else:
        return render(request, 'staff_apps/application_new.html', {
            'nav_page': 'support'
        })


@login_required(redirect_field_name=None)
def application_view(request, application_id):
    try:
        application_id = int(application_id)
    except ValueError:
        raise Http404()

    user = get_user(request)

    if application_id is None:
        raise Http404()

    application = StaffApplication.objects.filter(id=application_id).first()

    if application is None:
        raise Http404()

    if not (application.creator == user.uid or (user.is_staff and user.is_admin)):
        raise Http404()

    languages = []

    if application.languages_english:
        languages.append('english')
    elif application.languages_spanish:
        languages.append('spanish')
    elif application.languages_german:
        languages.append('german')
    elif application.languages_french:
        languages.append('french')
    elif application.languages_other:
        languages.append('other')

    return render(request, 'staff_apps/application_view.html', {
        'context': {
            'application': {
                'id': application.id,
                'creator': get_name_from_id(application.creator.__str__()),
                'status': application.status,
                'status_pretty': application.get_pretty_status(),
                'publish_date': application.publish_date.timestamp(),
                'updated_date': application.updated_date.timestamp(),
                'first_name': application.first_name,
                'telegram': application.telegram,
                'previous_experience': application.previous_experience,
                'about_yourself': application.about_yourself,
                'cheating_opinion': application.cheating_opinion,
                'region': application.get_pretty_region(),
                'activity': application.get_pretty_activity(),
                'languages': languages
            }
        }
    })


@login_required(redirect_field_name=None)
def applications_action(request):
    if 'action' not in request.POST or 'application_id' not in request.POST:
        return JsonResponse({'response': 'missing-fields'})

    application_id = request.POST['application_id']
    action = request.POST['action']

    try:
        application_id = int(application_id)
    except ValueError:
        raise Http404()

    if application_id is None:
        return JsonResponse({'response': 'missing-record'})

    application = StaffApplication.objects.filter(pk=application_id).first()

    if application is None:
        return JsonResponse({'response': 'missing-record'})

    user = get_user(request)

    if not (user.is_staff and user.is_admin):
        return JsonResponse({'response': 'unauthorized'})

    if action == 'interview':
        application.status = 'b'
    elif action == 'accept':
        application.status = 'c'
    elif action == 'deny':
        application.status = 'd'

    application.save()

    notification = SiteNotification()
    notification.user = application.creator
    notification.type = 'APPLICATIONS_STATUS_CHANGED'
    notification.data = {
        'application_id': application.id,
    }
    notification.save()

    return JsonResponse({'response': 'success'})
