from django.contrib.auth import get_user
from django.contrib.auth.decorators import login_required
from django.http import Http404
from django.shortcuts import render

from itertools import chain

from authentication.models import SiteUser
from data.models import DataHelper, DataConversion, get_name_from_id
from forums.models import Thread, Reply


def index(request):
    announcements = []
    recent_posts = []
    sorted_posts = []

    for thread in Thread.objects.filter(announcement=True).order_by('-publish_date')[:5]:
        announcements.append({
            'id': thread.thread_id,
            'title': thread.thread_title,
            'content': thread.thread_content,
            'creator_name': get_name_from_id(thread.thread_creator.__str__()),
            'publish_date': thread.publish_date.timestamp()
        })

    for post in chain(Thread.objects.order_by('-bumped_date')[:20], Reply.objects.order_by('-publish_date')[:20]):
        if isinstance(post, Thread):
            recent_posts.append({
                'type': 'thread',
                'data': {
                    'id': post.thread_id,
                    'title': post.thread_title,
                    'creator': get_name_from_id(post.thread_creator.__str__()),
                    'date': post.publish_date.timestamp(),
                }
            })
        else:
            thread = Thread.objects.filter(thread_id=post.thread_id).first()

            if thread is None:
                continue

            recent_posts.append({
                'type': 'reply',
                'data': {
                    'id': thread.thread_id,
                    'title': thread.thread_title,
                    'creator': get_name_from_id(post.reply_creator.__str__()),
                    'date': post.publish_date.timestamp()
                }
            })

    for post in sorted(recent_posts, key=lambda post: post['data']['date'], reverse=True)[:10]:
        sorted_posts.append(post)

    return render(request, 'base/index.html', {
        'context': {
            'navigation': 'home',
            'proxy_data': DataHelper.get_proxy_data(),
            'announcements': announcements,
            'announcements_size': len(announcements),
            'recent_posts': sorted_posts,
            'recent_posts_size': len(sorted_posts),
        }
    })


def player_index(request, username):
    if request.method == 'POST':
        raise Http404()

    data = DataHelper.get_profile_by_username(username, True)

    if data is None:
        return render(request, 'base/player/not_found.html')

    data['converted'] = {
        'practice': DataConversion.convert_practice_data(data['games']['practice']['statistics'])
    }

    site_user = SiteUser.objects.filter(uid=data['uuid']).first()

    recent_posts = []
    threads = 0
    replies = 0

    if site_user is not None:
        data['youtube_link'] = site_user.youtube_link
        data['twitter_link'] = site_user.twitter_link

        threads = Thread.objects.filter(thread_creator=site_user.uid).count()
        replies = Reply.objects.filter(reply_creator=site_user.uid).count()

        for post in sorted(chain(Thread.objects.filter(thread_creator=site_user.uid), Reply.objects.filter(reply_creator=site_user.uid)), key=lambda post: post.publish_date, reverse=True):
            if isinstance(post, Thread):
                recent_posts.append({
                    'type': 'thread',
                    'data': {
                        'id': post.thread_id,
                        'title': post.thread_title,
                        'content': post.thread_content.__str__(),
                        'publish_date': post.publish_date.timestamp()
                    }
                })
            else:
                thread = Thread.objects.filter(thread_id=post.thread_id).first()

                recent_posts.append({
                    'type': 'reply',
                    'data': {
                        'id': post.thread_id,
                        'title': None if thread is None else thread.thread_title,
                        'content': post.reply_content.__str__(),
                        'publish_date': post.publish_date.timestamp()
                    }
                })

    return render(request, 'base/player/index.html', {
        'context': {
            'navigation': 'profile',
            'profile': data,
            'forums': {
                'recent_posts': recent_posts,
                'recent_posts_size': len(recent_posts),
                'threads': threads,
                'replies': replies
            }
        }
    })


def leaderboards_index(request):
    ladders = ['NoDebuff', 'Debuff', 'BuildUHC', 'Sumo', 'Gapple', 'Soup', 'Archer', 'Combo', 'Vanilla']
    leaderboards = DataHelper.get_leaderboards_data()

    premium = leaderboards['Premium']
    premium_leaders = {
        '1': premium['entries'][0],
        '2': premium['entries'][1],
        '3': premium['entries'][2]
    }

    split = []
    split_set = []
    iterations = 0

    for ladder in ladders:
        if ladder == 'Premium':
            continue

        if iterations >= 3:
            split.append(split_set)
            split_set = []
            iterations = 0

        split_set.append(leaderboards[ladder])
        iterations = iterations + 1

    if len(split_set) > 0:
        split.append(split_set)

    return render(request, 'base/leaderboards/index.html', {
        'context': {
            'ladders': ladders,
            'leaderboards': split,
            'premium_leaders': premium_leaders
        }
    })


def staff_index(request):
    staff = DataHelper.get_staff_data()

    ranks = {
        'OWNER': {},
        'DEVELOPER': {},
        'MANAGER': {},
        'PLATFORM_ADMINISTRATOR': {},
        'ADMINISTRATOR': {},
        'SENIOR_MODERATOR': {},
        'MODERATOR': {},
        'TRIAL_MOD': {}
    }

    for rank in ranks:
        entries = []

        for staff_entry in staff[rank]:
            entries.append(staff_entry)

        if rank == 'OWNER' or rank == 'MANAGER':
            entries.reverse()

        ranks[rank] = {
            'name': rank,
            'entries': entries
        }

    return render(request, 'base/staff/index.html', {
        'context': {
            'navigation': 'staff',
            'staff': ranks
        }
    })


def partial_search_results(request, username):
    search_results = DataHelper.get_search_results(username)

    return render(request, 'base/global/search_results.html', {
        'context': {
            'search_results': search_results
        }
    })


@login_required(redirect_field_name=None)
def partial_player_staff(request, uuid):
    user = get_user(request)

    if not user.is_staff:
        raise Http404()

    alts = DataHelper.get_profile_alts(uuid)

    return render(request, 'base/player/partial/staff.html', {
        'context': {
            'alts': alts
        }
    })


@login_required(redirect_field_name=None)
def partial_player_lmlogs(request, uuid):
    user = get_user(request)

    if not user.can_view_logs:
        raise Http404()

    profile = DataHelper.get_profile_by_uuid(user.uid.__str__())

    if profile is None:
        raise Http404()

    logs = DataHelper.get_anticheat_logs(uuid)

    return render(request, 'base/player/partial/lmlogs.html', {
        'context': {
            'logs': logs,
            'logs_size': 0 if logs is None else len(logs)
        }
    })
