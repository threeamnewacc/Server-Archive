import string
import random

from django.shortcuts import render, redirect
from django.contrib.auth.models import AnonymousUser
from django.contrib.auth import authenticate, login, logout, get_user
from django.contrib.auth.decorators import login_required
from django.core.mail import send_mail
from django.http import Http404, JsonResponse

from authentication.models import SiteUser, SiteUserLog, SiteNotification
from data.models import get_from_api, post_to_api, DataHelper, DataConversion
from ipware.ip import get_ip


def auth_login(request):
    user = get_user(request)

    if not isinstance(user, AnonymousUser):
        return redirect('/')

    if request.method == 'POST':
        email = request.POST['email']
        password = request.POST['password']

        if email is None or password is None:
            return JsonResponse({'response': 'fail'})

        find_user = SiteUser.objects.filter(email__iexact=email).first()

        if find_user is None:
            return JsonResponse({'response': 'fail'})

        user_auth = authenticate(username=find_user.email, password=password)

        if user_auth is None:
            return JsonResponse({'response': 'fail'})
        else:
            login(request, user_auth)

            site_log = SiteUserLog()
            site_log.user = user_auth.uid
            site_log.ip_address = get_ip(request)
            site_log.log = 'Logged in'
            site_log.save()

            return JsonResponse({'response': 'success'})
    else:
        return render(request, 'authentication/login.html')


@login_required(redirect_field_name=None)
def auth_logout(request):
    logout(request)
    return redirect('/')


def auth_register(request, confirmation=None):
    if isinstance(request.user, AnonymousUser):
        if request.method == 'POST':
            if 'email_address' not in request.POST or 'confirmation' not in request.POST or 'password' not in request.POST or 'password_match' not in request.POST:
                return JsonResponse({'response': 'missing-fields'})

            email_address = request.POST['email_address']
            confirmation = request.POST['confirmation']
            password = request.POST['password']
            password_match = request.POST['password_match']

            retrieved = get_from_api('player/fetch_by_confirmation/' + confirmation)

            if retrieved is None or 'uuid' not in retrieved:
                return JsonResponse({'response': 'confirmation-not-found'})

            if retrieved['emailAddress'] != email_address:
                return JsonResponse({'response': 'pair-non-match'})

            if password != password_match:
                return JsonResponse({'response': 'password-non-match'})

            existing_user = SiteUser.objects.filter(uid=retrieved['uuid']).first()

            if existing_user is not None:
                print('already registered with uid: ' + existing_user.uid.__str__())
                return JsonResponse({'response': 'already-registered'})

            existing_user = SiteUser.objects.filter(email__iexact=email_address).first()

            if existing_user is not None:
                print('already registered with email: ' + existing_user.email)
                return JsonResponse({'response': 'already-registered'})

            register_response = post_to_api('player/update-register/', {
                'uuid': retrieved['uuid'],
                'emailAddress': email_address,
                'confirmationId': None,
                'registered': True
            }, None)

            if 'response' not in register_response or register_response['response'] != 'SUCCESS':
                return JsonResponse({'response': 'api-fail'})

            SiteUser.objects.create_user(email_address, retrieved['uuid'], password)

            return JsonResponse({'response': 'success'})
        else:
            retrieved = None

            if confirmation is not None:
                retrieved = get_from_api('player/fetch_by_confirmation/' + confirmation)

                if retrieved is None or 'uuid' not in retrieved:
                    retrieved = None

            return render(request, 'authentication/register.html', {
                'context': {
                    'navigation': 'register',
                    'confirmation': retrieved
                }
            })
    else:
        return redirect('/')


@login_required(redirect_field_name=None)
def auth_manage(request):
    user = get_user(request)

    profile = DataHelper.get_profile_by_uuid(user.uid.__str__())

    if profile is None:
        raise Http404()

    retrieved = get_from_api('client/cosmetics/get/' + profile['uuid'])

    cosmetics = {

    }

    return render(request, 'authentication/manage.html', {
        'context': {
            'navigation': 'account_manage',
            'profile': profile,
            'cosmetics': cosmetics
        }
    })


@login_required(redirect_field_name=None)
def auth_change_password(request):
    user = get_user(request)

    if request.method == 'POST':
        if 'password_current' not in request.POST or 'password_new' not in request.POST or 'password_new_match' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        password_current = request.POST['password_current']
        password_new = request.POST['password_new']
        password_new_match = request.POST['password_new_match']

        if not user.check_password(password_current):
            return JsonResponse({'response': 'invalid-old-password'})

        if password_new != password_new_match:
            return JsonResponse({'response': 'matching-new-password'})

        user.set_password(password_new_match)
        user.save()

        return JsonResponse({'response': 'success'})
    else:
        raise Http404()


def auth_forgot_password(request):
    user = get_user(request)

    if request.method == 'POST':
        if user is not None and isinstance(user, SiteUser):
            return JsonResponse({'response': 'unauthorized'})

        if 'email_address' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        email_address = request.POST['email_address']

        existing_user = SiteUser.objects.filter(email__iexact=email_address).first()

        if existing_user is None:
            return JsonResponse({'response': 'missing-record'})

        reset_confirmation = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(12))

        existing_user.reset_confirmation = reset_confirmation
        existing_user.save()

        send_mail(
            'Zonix Network - Reset Your Password',
            'Howdy!\n\nYour password reset link is:\nhttps://www.zonix.us/reset-password/' + reset_confirmation + '\n\nIf you did not request this password reset link, just ignore this email.',
            'site@zonix.us',
            [email_address],
            fail_silently=False,
        )

        return JsonResponse({'response': 'success'})

    else:
        if user is not None and isinstance(user, SiteUser):
            return redirect('/')
        else:
            return render(request, 'authentication/forgot-password.html')


def auth_reset_password(request, reset_confirmation=None):
    user = get_user(request)

    if request.method == 'POST':
        if user is not None and isinstance(user, SiteUser):
            return JsonResponse({'response': 'unauthorized'})

        if 'reset_confirmation' not in request.POST or 'new_password' not in request.POST or 'new_password_confirmation' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        reset_confirmation = request.POST['reset_confirmation']
        new_password = request.POST['new_password']
        new_password_confirmation = request.POST['new_password_confirmation']

        retrieved_user = SiteUser.objects.filter(reset_confirmation=reset_confirmation).first()

        if retrieved_user is None:
            return JsonResponse({'response': 'missing-record'})

        if retrieved_user.reset_confirmation != reset_confirmation:
            return JsonResponse({'response': 'non-match-confirmation'})

        if new_password != new_password_confirmation:
            return JsonResponse({'response': 'non-match-password'})

        retrieved_user.reset_confirmation = None
        retrieved_user.set_password(new_password_confirmation)
        retrieved_user.save()

        return JsonResponse({'response': 'success'})
    else:
        if reset_confirmation is None:
            print('cock1')
            raise Http404()

        if user is not None and isinstance(user, SiteUser):
            return redirect('/')
        else:
            retrieved_user = SiteUser.objects.filter(reset_confirmation=reset_confirmation).first()

            if retrieved_user is None:
                raise Http404()

            return render(request, 'authentication/reset-password.html', {
                'context': {
                    'reset_confirmation': reset_confirmation
                }
            })


@login_required(redirect_field_name=None)
def notification_seen(request):
    user = get_user(request)

    if isinstance(user, AnonymousUser):
        raise Http404()

    if request.method == 'POST':
        if 'notification_id' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        notification_id = request.POST['notification_id']

        notification = SiteNotification.objects.filter(id=notification_id).first()

        if notification is None:
            return JsonResponse({'response': 'missing-record'})

        if user.uid != notification.user:
            return JsonResponse({'response': 'unauthorized'})

        notification.seen = True
        notification.save()

        return JsonResponse({'response': 'success'})
    else:
        raise Http404()
