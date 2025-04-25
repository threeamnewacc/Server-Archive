from django.conf.urls import url

from . import views

app_name = 'base'

urlpatterns = [
    # Home
    url(r'^$', views.index, name='index'),
    # Player
    url(r'^player/(?P<username>[^/]+)', views.player_index, name='profile_index'),
    # Leaderboards
    url(r'^leaderboards/', views.leaderboards_index, name='leaderboards_index'),
    # Staff
    url(r'^staff', views.staff_index, name='staff_index'),
    # Search
    url(r'^partial/search_results/(?P<username>[^/]+)', views.partial_search_results),
    # Restricted
    url(r'^partial/player/staff/(?P<uuid>[^/]+)', views.partial_player_staff),
    url(r'^partial/player/lmlogs/(?P<uuid>[^/]+)', views.partial_player_lmlogs),
]