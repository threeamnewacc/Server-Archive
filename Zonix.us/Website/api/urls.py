from django.conf.urls import url

from . import views

app_name = 'api'

urlpatterns = [
    url(r'^client', views.client_download),
    url(r'^client/download', views.client_download),
    url(r'^api/get_announcements', views.api_get_announcements),
    url(r'^api/get_proxy_data', views.api_get_proxy_data),
    url(r'^api/client/libraries/(?P<library>[^/]+)', views.api_download_library),
    url(r'^api/client/natives/(?P<native>[^/]+)', views.api_download_native),
    url(r'^api/client/authentication/valid', views.api_auth_valid),
    url(r'^api/client/stream_bytes', views.api_stream_bytes),
    url(r'^api/client/default_settings', views.api_default_settings),
    url(r'^api/client/launcher_details', views.api_launcher_details),
    url(r'^api/client/cosmetics/get/(?P<uuid>[^/]+)', views.api_get_cosmetics),
    url(r'^api/client/cape/download/(?P<uuid>[^/]+)', views.api_download_cape),
    url(r'^api/client/hash', views.api_get_hash)
]
