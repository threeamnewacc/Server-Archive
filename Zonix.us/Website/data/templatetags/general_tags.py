from django import template
from data.models import get_name_from_id, DataConversion

register = template.Library()


@register.simple_tag()
def get_ratio(n1, n2):
    if n1 >= 0 and n2 == 0:
        return 100
    elif n1 == n2:
        return 50
    else:
        return round(n1 / n2)


@register.simple_tag()
def get_size_of(objects):
    return len(objects)


@register.simple_tag()
def get_head_link(uuid, size=32):
    return 'https://crafatar.com/avatars/' + uuid + '?size=' + size.__str__() + '&default=MHF_Steve'


@register.simple_tag()
def get_bust_link(uuid, size=256):
    return 'https://visage.surgeplay.com/bust/' + size.__str__() + '/' + uuid


@register.simple_tag()
def join_string(string_list):
    it = 0
    joined = ''

    for string in string_list:
        joined = (joined + string) if it == 0 else (joined + ', ' + string)
        it = it + 1

    return joined


@register.simple_tag()
def get_user_uuid(user):
    return user.uid.__str__()


@register.simple_tag()
def convert_rank_data(rank):
    return DataConversion.convert_rank_data(rank)


@register.simple_tag()
def shorten_string(string, max_length):
    if string is None or max_length is None:
        return None

    return string if len(string) <= max_length else (string[:max_length] + '...')


@register.simple_tag()
def assign_var(value):
    return value


@register.simple_tag()
def get_item(dict_or_list, key, default=None):
    try:
        if isinstance(dict_or_list, dict):
            return dict_or_list.get(key)
        else:
            return dict_or_list[int(key)]
    except IndexError:
        return default
    except KeyError:
        return default


@register.simple_tag()
def get_name_from_uuid(uuid):
    return get_name_from_id(uuid)


@register.simple_tag()
def get_name_from_user(user):
    return user.get_username()


@register.simple_tag()
def is_staff(user):
    return user.is_authenticated() is True and user.is_staff() is True


@register.simple_tag()
def is_admin(user):
    return user.is_authenticated() is True and user.is_staff() is True and user.is_admin is True


@register.simple_tag()
def get_length(data):
    return len(data)


@register.simple_tag()
def format_display_name(name):
    if name == 'nodebuff':
        return 'NoDebuff'
    elif name == 'hcf':
        return 'HCF'
    elif name == 'builduhc':
        return 'BuildUHC'
    elif name == 'redrover':
        return 'RedRover'
    elif name == 'oitc':
        return 'OITC'
    else:
        return name.title()


@register.simple_tag()
def get_icon_path(name):
    if name == 'premium':
        path = '/static/image/inventory/items/399-0.png'
    elif name == 'gapple':
        path = '/static/image/inventory/items/322-0.png'
    elif name == 'soup':
        path = '/static/image/inventory/items/282-0.png'
    elif name == 'vanilla':
        path = '/static/image/inventory/items/373-8229.png'
    elif name == 'nodebuff':
        path = '/static/image/inventory/items/373-16421.png'
    elif name == 'debuff':
        path = '/static/image/inventory/items/373-16420.png'
    elif name == 'archer':
        path = '/static/image/inventory/items/261-0.png'
    elif name == 'classic':
        path = '/static/image/inventory/items/276-0.png'
    elif name == 'axe':
        path = '/static/image/inventory/items/258-0.png'
    elif name == 'spleef':
        path = '/static/image/inventory/items/277-0.png'
    elif name == 'sumo':
        path = '/static/image/inventory/items/288-0.png'
    elif name == 'builduhc':
        path = '/static/image/inventory/items/327-0.png'
    elif name == 'combo':
        path = '/static/image/inventory/items/349-3.png'
    elif name == 'parkour':
        path = '/static/image/inventory/items/288-0.png'
    elif name == 'redrover':
        path = '/static/image/inventory/items/76-0.png'
    elif name == 'oitc':
        path = '/static/image/inventory/items/261-0.png'
    else:
        path = '/static/image/inventory/items/389-0.png'

    return path
