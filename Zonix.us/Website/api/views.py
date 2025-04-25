import os
import datetime
import hashlib

from django.http import JsonResponse, FileResponse, HttpResponse, Http404, HttpResponseRedirect

from forums.models import Thread
from data.models import DataHelper, get_name_from_id, get_from_api

from ipware.ip import get_ip
from pathlib import Path


def client_download(request):
    return HttpResponseRedirect("https://drive.google.com/uc?export=download&confirm=bPgx&id=16ZS_-OUsxCeTbD7RNomKWQb8sgjUQC4u")


def api_get_announcements(request):
    announcements = []

    for thread in Thread.objects.filter(announcement=True).order_by('-publish_date')[:5]:
        dt = datetime.datetime.fromtimestamp(thread.publish_date.timestamp())

        announcements.append({
            'id': thread.thread_id,
            'title': thread.thread_title,
            'content': thread.thread_content.__str__(),
            'creator_uuid': thread.thread_creator.__str__(),
            'creator_name': get_name_from_id(thread.thread_creator.__str__()),
            'publish_date': dt.strftime('%B %d %Y - %I:%M %p'),
            'url': 'https://www.zonix.us/forums/t/v/' + thread.thread_id.__str__()
        })

    return JsonResponse({
        'announcements': announcements
    })


def api_get_proxy_data(request):
    return JsonResponse(DataHelper.get_proxy_data())


def api_download_library(request, library):
    file = Path(os.getcwd() + '/api/client/libraries/' + library + '.jar')

    if file.exists() and file.is_file():
        return FileResponse(file.open('rb'))
    else:
        raise Http404()


def api_download_native(request, native):
    file = Path(os.getcwd() + '/api/client/natives/' + native + '.dll')

    if file.exists() and file.is_file():
        return FileResponse(file.open('rb'))
    else:
        raise Http404()


def api_auth_valid(request):
    ip = get_ip(request)

    if ip is None:
        return HttpResponse()
    else:
        response = {
            'ip': ip,
            'uuid': '77e8431a-dfc5-42ab-98e1-e018e48a734b',
            'note': 'gay'
        }

        return JsonResponse(response)


def api_stream_bytes(request):
    ip = get_ip(request)

    if ip is None:
        return HttpResponse()
    else:
        if ip == '172.220.214.211':
            return FileResponse(open(os.getcwd() + '/api/client/dev.jar', 'rb'))
        else:
            return FileResponse(open(os.getcwd() + '/api/client/launcher.jar', 'rb'))


def api_get_cosmetics(request, uuid):
    response = get_from_api('client/cosmetics/get/' + uuid)

    if response is None:
        return HttpResponse()

    return JsonResponse(response)


def api_download_cape(request, uuid):
    response = get_from_api('client/cosmetics/get/' + uuid)

    if response is None:
        return HttpResponse()

    if 'cape' not in response:
        return HttpResponse()

    file = Path(os.getcwd() + '/static/image/capes/' + response['cape'] + '.png')

    if file.exists() and file.is_file():
        image_data = open(os.getcwd() + '/static/image/capes/' + response['cape'] + '.png', 'rb').read()
        return HttpResponse(image_data, content_type='image/png')
    else:
        return HttpResponse('none')


def api_get_hash(request):
    sha1 = hashlib.sha1()

    with open(os.getcwd() + '/api/client/launcher.jar', 'rb') as f:
        while True:
            data = f.read(65536)

            if not data:
                break

            sha1.update(data)

    return HttpResponse(sha1.hexdigest())


def api_default_settings(request):
    return FileResponse(open(os.getcwd() + '/api/client/default.json', 'rb'))


def api_launcher_details(request):
    return FileResponse(open(os.getcwd() + '/api/client/launcher.json', 'rb'))
