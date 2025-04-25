from django.conf.urls import url

from . import views

app_name = 'admin_panel'

urlpatterns = [
    # Index
    url(r'^$', views.admin_index, name='admin_index'),
    # Tickets
    url(r'^tickets/ticket-blacklist', views.ticket_blacklist),
    url(r'^tickets/all/(?P<page>[^/]+)/(?P<sort>\w+)', views.tickets_all),
    url(r'^tickets/all/(?P<page>[^/]+)', views.tickets_all),
    url(r'^tickets/all', views.tickets_all),
    url(r'^tickets', views.tickets_index),
    # Applications
    url(r'^applications/all/(?P<page>[^/]+)/(?P<sort>\w+)', views.applications_all),
    url(r'^applications/all/(?P<page>[^/]+)', views.applications_all),
    url(r'^applications/all', views.applications_all),
    # Staff
    url(r'^staff/tickets', views.tickets_handling),
]
