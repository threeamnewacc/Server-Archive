from django.conf.urls import url

from . import views


app_name = 'support'

urlpatterns = [
    url(r'^$', views.support_index, name='support_index'),
    url(r'^tickets/new/', views.tickets_new, name='tickets_new'),
    url(r'^tickets/v/(?P<ticket_id>[^/]+)', views.tickets_view, name='tickets_view'),
    url(r'^tickets/r/', views.tickets_reply, name='tickets_reply'),
    url(r'^tickets/c/(?P<ticket_id>[^/]+)', views.tickets_close, name='tickets_close'),
    url(r'^tickets/o/(?P<ticket_id>[^/]+)', views.tickets_open, name='tickets_reassign'),
    url(r'^tickets/ra/(?P<ticket_id>[^/]+)', views.tickets_reassign, name='tickets_reassign'),
    url(r'^tickets/d/(?P<ticket_id>[^/]+)', views.tickets_delete, name='tickets_delete'),
    url(r'^tickets/', views.tickets_index, name='tickets_index'),
]
