import datetime

from django import template
from django.utils.safestring import mark_safe

register = template.Library()


@register.simple_tag()
def has_expired(timestamp, addition=None):
    now = datetime.datetime.now().timestamp()

    timestamp = int(timestamp)

    if addition is not None:
        addition = int(addition)

        if addition == -1:
            return False

        timestamp = timestamp + addition

    return (timestamp / 1000) < now


@register.simple_tag()
def get_iso8601_from_epoch(timestamp, addition=None, divide=True):
    if timestamp is None:
        return ''

    timestamp = int(timestamp)

    if addition is not None:
        timestamp = timestamp + addition

    if divide:
        timestamp = timestamp / 1000

    return datetime.datetime.fromtimestamp(timestamp).isoformat() + '-0500'


@register.simple_tag()
def timeago_helper(timestamp, divide=False):
    if timestamp is None:
        return ''

    timestamp = int(timestamp)

    if divide:
        timestamp = int(timestamp / 1000)

    if timestamp == -1:
        return 'Unknown'
    else:
        return mark_safe('<time class="timeago" datetime="' + get_iso8601_from_epoch(timestamp, None, False) + '"></time>')


@register.simple_tag()
def format_timestamp(timestamp, strp_format, divide=True):
    if timestamp is None or strp_format is None:
        return None

    timestamp = int(timestamp)

    if divide:
        timestamp = timestamp / 1000

    dt = datetime.datetime.fromtimestamp(timestamp)

    return dt.strftime(strp_format)


@register.simple_tag()
def format_datetime(dt, strp_format):
    if dt is None or strp_format is None:
        return None

    return dt.strftime(strp_format)
