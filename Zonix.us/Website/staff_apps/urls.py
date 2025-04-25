from django.conf.urls import url

from . import views

app_name = 'staff_apps'

urlpatterns = [
    # Index
    url(r'^$', views.applications_index, name='applications_index'),
    # View
    url(r'^v/(?P<application_id>[^/]+)', views.application_view),
    # New
    url(r'^new', views.application_new),
    # Action
    url(r'^action', views.applications_action)
]