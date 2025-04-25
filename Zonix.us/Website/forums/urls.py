from django.conf.urls import url

from . import views

app_name = 'forums'

urlpatterns = [
    # Index
    url(r'^$', views.forums_index, name='forums_index'),
    # Create Category
    # url(r'^category/create/', views.forums_category_create, name='forums_category_create'),
    # Create Forum
    # url(r'^f/c/', views.forums_forum_create, name='forums_forum_create'),
    # View Forum
    url(r'^f/v/(?P<forum_id>[^/]+)/(?P<page>[^/]+)', views.forums_forum_view),
    url(r'^f/v/(?P<forum_id>[^/]+)', views.forums_forum_view),
    # View Thread
    url(r'^t/v/(?P<thread_id>[^/]+)/(?P<page>[^/]+)/', views.forums_thread_view),
    url(r'^t/v/(?P<thread_id>[^/]+)', views.forums_thread_view),
    # Create Thread
    url(r'^t/c/(?P<forum_id>[^/]+)/', views.forums_thread_create),
    # Reply Thread
    url(r'^t/r/(?P<thread_id>[^/]+)/', views.forums_thread_reply),
    # Modify Thread
    # url(r'^thread/modify/(?P<thread_id>[^/]+)', views.forums_thread_modify, name='forums_thread_modify'),
    # Lock Thread
    url(r'^t/lock/', views.forums_thread_lock),
    # Unlock Thread
    url(r'^t/unlock/', views.forums_thread_unlock),
    # Delete Post
    url(r'^p/delete/', views.forums_post_delete),
    # Announce Thread
    url(r'^t/announce/', views.forums_thread_announce),
    # Sticky Thread
    url(r'^t/sticky/', views.forums_thread_sticky),
    # Get Thread Partial
    url(r'^partial/thread_reply/(?P<reply_id>[^/]+)/', views.forums_partial_thread_reply),
]
