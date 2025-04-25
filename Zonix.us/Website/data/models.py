import json
import requests
import datetime

from django.core.cache import cache
from django.core.paginator import Paginator

from json import JSONDecodeError

api_url = 'http://66.70.176.35:2583/api/'
api_key = 'ce07a742-cccc-4888-b595-a62a7dd3bb19'


def get_from_api(path):
    if path is None:
        raise ValueError('Must provide path')

    response = requests.get(api_url + api_key + '/' + path)

    if not response.text:
        return None

    try:
        return json.loads(response.text)
    except JSONDecodeError:
        return response.text


def post_to_api(path, params=None, json_body=None):
    if path is None:
        raise ValueError('Must provide path')

    response = requests.post(api_url + api_key + '/' + path, params, json_body)

    if not response.text:
        return None

    try:
        return json.loads(response.text)
    except JSONDecodeError:
        return response.text


def get_name_from_id(uuid):
    if uuid is None:
        raise ValueError('Must provide ID')

    name = cache.get('name_' + uuid.__str__())

    if name is None:
        data = DataHelper.get_profile_by_uuid(uuid)

        if data is not None:
            if 'name' in data:
                name = data['name']
                cache.set('name_' + uuid.__str__(), name, 60 * 5)
            else:
                name = None

    if name is None:
        name = None

    return name


def get_rank_from_id(uuid):
    if uuid is None:
        raise ValueError('Must provide ID')

    rank = cache.get('rank_' + uuid.__str__())

    if rank is None:
        data = DataHelper.get_profile_by_uuid(uuid)

        if data is not None:
            if 'name' in data:
                rank = data['rank']
                cache.set('rank_' + uuid.__str__(), rank, 60 * 2)
            else:
                rank = 'DEFAULT'

    if rank is None:
        rank = 'DEFAULT'

    return rank


def get_or_default(data, key, default):
    if data is None or key is None:
        return default

    if isinstance(data, dict):
        if key in data:
            return data[key]
        else:
            return default
    else:
        return default


def compare_int(o1, o2):
    if o1 > o2:
        return 1
    elif o1 == o2:
        return -1
    else:
        return 0


class DataHelper:
    @staticmethod
    def get_search_results(username):
        return get_from_api('player/fetch_by_name_similar/' + username)

    @staticmethod
    def get_anticheat_logs(uuid):
        logs = get_from_api('anticheat/fetch_by_uuid/' + uuid)
        logs.reverse()

        return logs

    @staticmethod
    def get_profile_alts(uuid):
        return get_from_api('player/fetch_by_ip/' + uuid)

    @staticmethod
    def get_staff_data():
        staff_data = cache.get('staff_data')

        if staff_data is None:
            staff_data = post_to_api('admin/fetch-players-by-ranks', {
                'ranks': json.dumps(['OWNER', 'DEVELOPER', 'MANAGER', 'PLATFORM_ADMINISTRATOR', 'ADMINISTRATOR', 'SENIOR_MODERATOR', 'MODERATOR', 'TRIAL_MOD'])
            })

            if staff_data is None:
                return None
            else:
                cache.set('staff_data', staff_data, 60 * 5)

        return staff_data

    @staticmethod
    def get_admin_data():
        return get_from_api('admin/fetch-stats')

    @staticmethod
    def get_proxy_data():
        proxy_data = cache.get('proxy_data')

        if proxy_data is None:
            proxy_data = get_from_api('admin/fetch-proxy-data')

            if proxy_data is None:
                return None
            else:
                conversion = proxy_data

                if 'us-proxy-count' not in conversion or 'eu-proxy-count' not in conversion or 'sa-proxy-count' not in conversion or 'as-proxy-count' not in conversion:
                    return {
                        'us_proxy': 0,
                        'eu_proxy': 0,
                        'sa_proxy': 0,
                        'as_proxy': 0
                    }

                proxy_data = {
                    'us_proxy': conversion['us-proxy-count'],
                    'eu_proxy': conversion['eu-proxy-count'],
                    'sa_proxy': conversion['sa-proxy-count'],
                    'as_proxy': conversion['as-proxy-count']
                }

                cache.set('proxy_data', proxy_data, 20)

        return proxy_data

    @staticmethod
    def convert_profile(profile, extra):
        if extra:
            punishments = get_from_api('punishment/fetch_by_uuid/' + profile['uuid'])

            if punishments is None:
                punishments = []

            bans = []
            mutes = []

            is_banned = False
            is_muted = False
            ban_reason = None
            mute_reason = None

            for punishment in punishments:
                added_at = int(punishment['added_at'])
                duration = int(punishment['duration'])

                if duration == -1:
                    has_expired = False
                else:
                    has_expired = (added_at + duration) / 1000 < datetime.datetime.now().timestamp()

                if punishment['type'] == 'BAN' or punishment['type'] == 'TEMPBAN' or punishment['type'] == 'BLACKLIST':
                    bans.append(punishment)

                    if punishment['removed_reason'] is None and not has_expired:
                        is_banned = True
                        ban_reason = punishment['reason']
                else:
                    mutes.append(punishment)

                    if punishment['removed_reason'] is None and not has_expired:
                        is_muted = True
                        mute_reason = punishment['reason']

            profile['punishments'] = {
                'bans': bans,
                'bans_size': len(bans),
                'mutes': mutes,
                'mutes_size': len(mutes),
                'is_banned': is_banned,
                'ban_reason': ban_reason,
                'is_muted': is_muted,
                'mute_reason': mute_reason
            }

            profile['status'] = get_from_api('player/get_status/' + profile['uuid'])
            profile['games'] = {
                'practice': {
                    'statistics': get_from_api('practice/fetch_by_uuid/' + profile['uuid']),
                    'achievements': {},
                    'matches': get_from_api('practice/match/fetch_by_uuid/' + profile['uuid'])
                }
            }

        return profile

    @staticmethod
    def get_profile_by_username(username, extra):
        profile = get_from_api('player/fetch_by_name/' + username)

        if profile is None:
            return None

        return DataHelper.convert_profile(profile, extra)

    @staticmethod
    def get_profile_by_uuid(uuid, extra=False):
        profile = get_from_api('player/fetch_by_uuid/' + uuid)

        if profile is None:
            return None

        return DataHelper.convert_profile(profile, extra)

    @staticmethod
    def get_leaderboards_data():
        leaderboards = cache.get('leaderboards')

        if leaderboards is None:
            leaderboards = get_from_api('practice/fetch_leaderboards')

            if leaderboards is None:
                return None
            else:
                cache.set('leaderboards', leaderboards, 60 * 15)

        return leaderboards

    @staticmethod
    def generate_pagination(objects=None, page=1, count=10, sort=None):
        if objects is None:
            return None

        paginator = Paginator(objects, count)
        current_page = page
        max_pages = paginator.num_pages

        if current_page > max_pages:
            return None

        before_pages = []
        after_pages = []

        if current_page == 1:
            for i in range(1, 5):
                if current_page + i <= max_pages:
                    after_pages.append(current_page + i)
        else:
            for i in range(1, 5):
                if current_page - i > 0:
                    before_pages.append(current_page - i)

                if current_page + i <= max_pages:
                    after_pages.append(current_page + i)

        before_pages.reverse()

        pages = []

        if current_page - 5 > 1:
            pages.append('min')

        for page in before_pages:
            pages.append(page)

        pages.append('current')

        for page in after_pages:
            pages.append(page)

        if current_page + 5 < max_pages:
            pages.append('max')

        paginated = []

        for obj in paginator.page(current_page).object_list:
            paginated.append(obj)

        return {
            'objects': paginated,
            'objects_size': len(paginated),
            'paginated_size': len(paginated),
            'current_page': current_page,
            'min_pages': 1,
            'max_pages': max_pages,
            'pages': pages,
            'sort': '' if sort is None else sort
        }


class DataConversion:
    @staticmethod
    def convert_punishment_data(data):
        if data is None:
            return None

        bans = []
        mutes = []

        is_banned = False
        is_muted = False
        ban_reason = None
        mute_reason = None

        for punishment in data:
            added_at = int(punishment['added_at'])
            duration = int(punishment['duration'])

            if duration == -1:
                has_expired = False
            else:
                has_expired = (added_at + duration) / 1000 < datetime.datetime.now().timestamp()

            if punishment['type'] == 'BAN' or punishment['type'] == 'TEMPBAN' or punishment['type'] == 'BLACKLIST':
                bans.append(punishment)

                if punishment['removed_reason'] is None and not has_expired:
                    is_banned = True
                    ban_reason = punishment['reason']
            else:
                mutes.append(punishment)

                if punishment['removed_reason'] is None and not has_expired:
                    is_muted = True
                    mute_reason = punishment['reason']

        return {
            'bans': bans,
            'bans_size': len(bans),
            'mutes': mutes,
            'mutes_size': len(mutes),
            'is_banned': is_banned,
            'ban_reason': ban_reason,
            'is_muted': is_muted,
            'mute_reason': mute_reason
        }

    @staticmethod
    def convert_practice_data(data):
        if data is None:
            return None

        default = [
            'nodebuff',
            'debuff',
            'builduhc',
            'sumo',
            'classic',
            'axe',
            'vanilla',
            'soup',
            'gapple',
            'spleef',
            'combo',
            'archer'
        ]

        ladders = {}

        for ladder in default:
            ladders[ladder] = {
                'name': ladder,
                'wins': data[ladder + 'Wins'],
                'losses': data[ladder + 'Losses'],
                'elo': data[ladder + 'Elo'],
                'party_elo': data[ladder + 'EloParty']
            }

        statistics = {
            'ladders': ladders,
            'events': {
                'oitc': {
                    'name': 'oitc',
                    'kills': data['oitcEventKills'],
                    'deaths': data['oitcEventDeaths'],
                    'wins': data['oitcEventWins'],
                    'losses': data['oitcEventLosses']
                },
                'sumo': {
                    'name': 'sumo',
                    'wins': data['sumoEventWins'],
                    'losses': data['sumoEventLosses']
                },
                'redrover': {
                    'name': 'redrover',
                    'wins': data['redroverEventWins'],
                    'losses': data['redroverEventLosses']
                },
                'parkour': {
                    'name': 'parkour',
                    'wins': data['parkourEventWins'],
                    'losses': data['parkourEventLosses']
                }
            }
        }

        return statistics

    @staticmethod
    def convert_rank_data(rank):
        name = rank
        css_class = rank

        if rank == 'DEFAULT':
            name = 'Default'
            css_class = 'default'
        elif rank == 'E_GIRL':
            name = 'Friend'
            css_class = 'pink'
        elif rank == 'SILVER':
            name = 'Silver'
            css_class = 'silver'
        elif rank == 'GOLD':
            name = 'Gold'
            css_class = 'gold'
        elif rank == 'PLATINUM':
            name = 'Platinum'
            css_class = 'platinum'
        elif rank == 'EMERALD':
            name = 'Emerald'
            css_class = 'emerald'
        elif rank == 'ZONIX':
            name = 'Zonix'
            css_class = 'zonix'
        elif rank == 'BUILDER':
            name = 'Builder'
            css_class = 'builder'
        elif rank == 'MEDIA':
            name = 'YouTuber'
            css_class = 'pink'
        elif rank == 'PARTNER':
            name = 'Partner'
            css_class = 'partner'
        elif rank == 'FAMOUS':
            name = 'Famous'
            css_class = 'famous'
        elif rank == 'TRIAL_MOD':
            name = 'Trial-Mod'
            css_class = 'trial'
        elif rank == 'MODERATOR':
            name = 'Moderator'
            css_class = 'moderator'
        elif rank == 'SENIOR_MODERATOR':
            name = 'Sr. Moderator'
            css_class = 'sr-moderator'
        elif rank == 'ADMINISTRATOR':
            name = 'Administrator'
            css_class = 'administrator'
        elif rank == 'MEDIA_ADMIN':
            name = 'Administrator'
            css_class = 'administrator'
        elif rank == 'PLATFORM_ADMINISTRATOR':
            name = 'Platform Admin'
            css_class = 'administrator'
        elif rank == 'MANAGER':
            name = 'Manager'
            css_class = 'manager'
        elif rank == 'DEVELOPER':
            name = 'Developer'
            css_class = 'developer'
        elif rank == 'OWNER':
            name = 'Owner'
            css_class = 'owner'
        elif rank == 'MEDIA_OWNER':
            name = 'Owner'
            css_class = 'owner'
        elif rank == 'MEDIA_ADMIN':
            name = 'Administrator'
            css_class = 'administrator'
        elif rank == 'INCOGNITO':
            name = 'Retired Staff'
            css_class = 'famous'

        return {
            'name': name,
            'css_class': css_class
        }
