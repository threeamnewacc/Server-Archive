from django.http import Http404, JsonResponse
from django.shortcuts import render
from django.contrib.auth import get_user
from django.contrib.auth.decorators import login_required

from profanity import profanity

from .models import Category, Forum, Thread, Reply, PostLike
from data.models import DataHelper, get_name_from_id
from authentication.models import SiteNotification


def maintenance(request):
    return render(request, 'forums/maintenance.html')


def forums_index(request):
    categories = []

    for category in Category.objects.order_by('category_order'):
        forums = []

        for forum in Forum.objects.filter(category_id=category.category_id).order_by('forum_order'):
            replies_count = 0

            for thread in Thread.objects.filter(forum_id=forum.forum_id):
                replies_count = replies_count + Reply.objects.filter(thread_id=thread.thread_id).count()

            thread = Thread.objects.filter(forum_id=forum.forum_id).order_by('-bumped_date').first()

            if thread is None:
                last_thread = None
            else:
                reply = Reply.objects.filter(thread_id=thread.thread_id).order_by('-publish_date').first()

                if reply is None:
                    last_reply = None
                else:
                    last_reply = {
                        'creator_name': get_name_from_id(reply.reply_creator.__str__()),
                        'publish_date': reply.publish_date.timestamp()
                    }

                last_thread = {
                    'id': thread.thread_id,
                    'title': thread.thread_title,
                    'creator_name': get_name_from_id(thread.thread_creator.__str__()),
                    'publish_date': thread.publish_date.timestamp(),
                    'last_reply': last_reply
                }

            forums.append({
                'id': forum.forum_id,
                'title': forum.forum_title,
                'description': forum.forum_description,
                'last_thread': last_thread,
                'threads_count': Thread.objects.filter(forum_id=forum.forum_id).count(),
                'replies_count': replies_count
            })

        categories.append({
            'id': category.category_id,
            'title': category.category_title,
            'forums': forums,
            'forums_size': len(forums)
        })

    return render(request, 'forums/index.html', {
        'context': {
            'categories': categories,
            'categories_size': len(categories),
        }
    })


def forums_forum_view(request, forum_id, page=1):
    forum = Forum.objects.filter(forum_id=forum_id).first()

    if forum is None:
        raise Http404()

    category = Category.objects.filter(category_id=forum.category_id).first()

    if category is None:
        raise Http404()

    stickied = []

    for thread in Thread.objects.filter(forum_id=forum_id, stickied=True).order_by('-publish_date'):
        last_reply = thread.get_last_reply()

        stickied.append({
            'id': thread.thread_id,
            'title': thread.thread_title,
            'creator_name': get_name_from_id(thread.thread_creator.__str__()),
            'last_update': thread.bumped_date.timestamp(),
            'publish_date': thread.publish_date.timestamp(),
            'replies_count': Reply.objects.filter(thread_id=thread.thread_id).count(),
            'last_reply_timestamp': None if last_reply is None else last_reply.publish_date.timestamp(),
            'last_reply_creator': None if last_reply is None else get_name_from_id(last_reply.reply_creator.__str__())
        })

    pagination = DataHelper.generate_pagination(Thread.objects.filter(forum_id=forum_id, stickied=False).order_by('-publish_date'), int(page), 20)

    threads = []

    for thread in pagination['objects']:
        last_reply = thread.get_last_reply()

        threads.append({
            'id': thread.thread_id,
            'title': thread.thread_title,
            'creator_name': get_name_from_id(thread.thread_creator.__str__()),
            'last_update': thread.bumped_date.timestamp(),
            'publish_date': thread.publish_date.timestamp(),
            'replies_count': Reply.objects.filter(thread_id=thread.thread_id).count(),
            'last_reply_timestamp': None if last_reply is None else last_reply.publish_date.timestamp(),
            'last_reply_creator': None if last_reply is None else get_name_from_id(last_reply.reply_creator.__str__())
        })

    return render(request, 'forums/forum_view.html', {
        'context': {
            'forum': {
                'id': forum.forum_id,
                'title': forum.forum_title,
                'description': forum.forum_description
            },
            'stickied': stickied,
            'stickied_size': len(stickied),
            'threads': threads,
            'threads_size': len(threads),
            'pagination': pagination
        }
    })


def forums_thread_view(request, thread_id, page=1):
    thread = Thread.objects.filter(thread_id=thread_id).first()

    if thread is None:
        raise Http404()

    forum = Forum.objects.filter(forum_id=thread.forum_id).first()

    if forum is None:
        raise Http404()

    category = Category.objects.filter(category_id=forum.category_id).first()

    if category is None:
        raise Http404()

    pagination = DataHelper.generate_pagination(Reply.objects.filter(thread_id=thread_id).order_by('publish_date'), int(page), 10)

    profile_lookup = [thread.thread_creator.__str__()]
    replies = []

    for reply in pagination['objects']:
        likes = []

        for post_like in PostLike.objects.filter(post_id=('reply-' + reply.reply_id.__str__())):
            likes.append(post_like.reply_creator)

        replies.append({
            'id': reply.reply_id,
            'content': reply.reply_content,
            'creator': reply.reply_creator.__str__(),
            'publish_date': reply.publish_date.timestamp(),
            'likes': {
                'entries': likes,
                'size': len(likes)
            }
        })

        creator_uuid = reply.reply_creator.__str__()

        if creator_uuid not in profile_lookup:
            profile_lookup.append(creator_uuid)

    profiles = {}

    for lookup in profile_lookup:
        profile = DataHelper.get_profile_by_uuid(lookup)
        profile['threads_count'] = Thread.objects.filter(thread_creator=lookup).count()
        profile['replies_count'] = Reply.objects.filter(reply_creator=lookup).count()
        profiles[lookup] = profile

    likes = []

    for post_like in PostLike.objects.filter(post_id=('thread-' + thread.thread_id.__str__())):
        likes.append(post_like.reply_creator)

    return render(request, 'forums/thread_view.html', {
        'context': {
            'forum': {
                'id': forum.forum_id,
                'title': forum.forum_title,
                'description': forum.forum_description,
            },
            'thread': {
                'id': thread.thread_id,
                'title': thread.thread_title,
                'content': thread.thread_content,
                'creator': thread.thread_creator.__str__(),
                'publish_date': thread.publish_date.timestamp(),
                'locked': thread.locked,
                'likes': {
                    'entries': likes,
                    'size': len(likes)
                }
            },
            'replies': replies,
            'replies_size': len(replies),
            'profiles': profiles,
            'pagination': pagination
        }
    })


@login_required(redirect_field_name=None)
def forums_thread_create(request, forum_id):
    user = get_user(request)

    forum = Forum.objects.filter(forum_id=forum_id).first()

    if forum is None:
        raise Http404()

    if forum.requires_admin and (not user.is_staff or not user.is_admin):
        raise Http404()

    category = Category.objects.filter(category_id=forum.category_id).first()

    if category is None:
        raise Http404()

    if request.method == 'POST':
        profile = DataHelper.get_profile_by_uuid(user.uid.__str__(), True)

        if profile is None or profile['punishments']['is_banned']:
            return JsonResponse({'response': 'denied'})

        if 'thread_title' not in request.POST or 'thread_content' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        thread_title = request.POST['thread_title']
        thread_content = request.POST['thread_content']

        if profanity.contains_profanity(thread_title) or profanity.contains_profanity(thread_content):
            return JsonResponse({'response': 'profanity-filter'})

        thread = Thread()
        thread.forum_id = forum_id
        thread.thread_title = thread_title
        thread.thread_content = thread_content
        thread.thread_creator = user.uid.__str__()
        thread.save()

        return JsonResponse({'response': 'success', 'thread_id': thread.thread_id})
    else:
        return render(request, 'forums/thread_create.html', {
            'context': {
                'forum': {
                    'id': forum.forum_id,
                    'title': forum.forum_title,
                    'description': forum.forum_description
                }
            }
        })


@login_required(redirect_field_name=None)
def forums_thread_reply(request, thread_id):
    thread = Thread.objects.filter(thread_id=thread_id).first()

    if thread is None:
        return JsonResponse({'response': 'missing-record'})

    if request.method == 'POST':
        user = get_user(request)

        profile = DataHelper.get_profile_by_uuid(user.uid.__str__(), True)

        if profile is None or profile['punishments']['is_banned']:
            return JsonResponse({'response': 'denied'})

        if 'reply_content' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        if thread.locked:
            return JsonResponse({'response': 'thread-locked'})

        reply_content = request.POST['reply_content']

        if profanity.contains_profanity(reply_content):
            return JsonResponse({'response': 'profanity-filter'})

        reply = Reply()
        reply.thread_id = thread.thread_id
        reply.reply_content = reply_content
        reply.reply_creator = user.uid.__str__()
        reply.save()

        if reply.reply_creator != thread.thread_creator:
            notification = SiteNotification()
            notification.user = thread.thread_creator
            notification.type = 'FORUMS_REPLY_TO_THREAD'
            notification.data = {
                'reply_creator': reply.reply_creator,
                'thread_id': thread.thread_id,
                'thread_title': thread.thread_title
            }
            notification.save()

        return JsonResponse({'response': 'success', 'reply_id': reply.reply_id})
    else:
        raise Http404()


@login_required(redirect_field_name=None)
def forums_post_delete(request):
    if request.method == 'POST':
        user = get_user(request)

        if 'post_data' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        post_data = request.POST['post_data']
        post_data_split = post_data.split('-')
        post_data_id = int(post_data_split[1])

        if post_data.startswith('thread-'):
            thread = Thread.objects.filter(thread_id=post_data_id).first()

            if thread is None:
                return JsonResponse({'response': 'missing-record'})

            if user.is_staff and user.is_admin:
                for reply in Reply.objects.filter(thread_id=thread.thread_id):
                    reply.delete()

                thread.delete()

                return JsonResponse({'response': 'success'})
            else:
                return JsonResponse({'response': 'unauthorized'})
        elif post_data.startswith('reply-'):
            reply = Reply.objects.filter(reply_id=post_data_id).first()

            if reply is None:
                return JsonResponse({'response': 'missing-record'})

            if user.is_staff and user.is_admin:
                reply.delete()

                return JsonResponse({'response': 'success'})
            else:
                return JsonResponse({'response': 'unauthorized'})

        return JsonResponse({'response': 'unknown-post'})
    else:
        raise Http404()


@login_required(redirect_field_name=None)
def forums_thread_lock(request):
    if request.method == 'POST':
        user = get_user(request)

        if 'thread_id' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        thread = Thread.objects.filter(thread_id=request.POST['thread_id']).first()

        if thread is None:
            return JsonResponse({'response': 'missing-record'})

        if user.is_staff:
            thread.locked = True
            thread.save()

            return JsonResponse({'response': 'success'})
        else:
            return JsonResponse({'response': 'unauthorized'})


@login_required(redirect_field_name=None)
def forums_thread_unlock(request):
    if request.method == 'POST':
        user = get_user(request)

        if 'thread_id' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        thread = Thread.objects.filter(thread_id=request.POST['thread_id']).first()

        if thread is None:
            return JsonResponse({'response': 'missing-record'})

        if user.is_staff:
            thread.locked = False
            thread.save()

            return JsonResponse({'response': 'success'})
        else:
            return JsonResponse({'response': 'unauthorized'})


@login_required(redirect_field_name=None)
def forums_thread_announce(request):
    if request.method == 'POST':
        user = get_user(request)

        if 'thread_id' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        thread = Thread.objects.filter(thread_id=request.POST['thread_id']).first()

        if thread is None:
            return JsonResponse({'response': 'missing-record'})

        if user.is_staff and user.is_admin:
            thread.announcement = True
            thread.save()

            return JsonResponse({'response': 'success'})
        else:
            return JsonResponse({'response': 'unauthorized'})


@login_required(redirect_field_name=None)
def forums_thread_sticky(request):
    if request.method == 'POST':
        user = get_user(request)

        if 'thread_id' not in request.POST:
            return JsonResponse({'response': 'missing-fields'})

        thread = Thread.objects.filter(thread_id=request.POST['thread_id']).first()

        if thread is None:
            return JsonResponse({'response': 'missing-record'})

        if user.is_staff and user.is_admin:
            thread.stickied = True
            thread.save()

            return JsonResponse({'response': 'success'})
        else:
            return JsonResponse({'response': 'unauthorized'})


def forums_partial_thread_reply(request, reply_id):
    if request.method == 'POST':
        raise Http404()

    reply = Reply.objects.filter(reply_id=reply_id).first()

    if reply is None:
        raise Http404()

    profile = DataHelper.get_profile_by_uuid(reply.reply_creator.__str__())
    profile['threads_count'] = Thread.objects.filter(thread_creator=reply.reply_creator).count()
    profile['replies_count'] = Reply.objects.filter(reply_creator=reply.reply_creator).count()

    return render(request, 'forums/partial/thread_post.html', {
        # shitty context layout because partial is used many times w/ different contexts
        'context': {
            'profiles': {
                reply.reply_creator.__str__(): profile
            }
        },
        'reply': {
            'id': reply.reply_id,
            'content': reply.reply_content,
            'creator': reply.reply_creator.__str__(),
            'publish_date': reply.publish_date.timestamp()
        }
    })
