from django.contrib.auth import get_user
from django.contrib.auth.models import AnonymousUser

from .models import SiteNotification


def notifications(request):
    user = get_user(request)

    if isinstance(user, AnonymousUser):
        return {}

    if user is None:
        return {}

    notifications = []

    for notification in SiteNotification.objects.filter(user=user.uid, seen=False).order_by('-datetime')[:5]:
        notifications.append({
            'id': notification.id,
            'type': notification.type,
            'data': notification.data,
            'timestamp': notification.datetime.timestamp()
        })

    notifications_count = SiteNotification.objects.filter(user=user.uid, seen=False).count()

    return {
        'notifications': notifications,
        'notifications_count': notifications_count
    }