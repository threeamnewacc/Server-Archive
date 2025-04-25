from django.conf.urls import url

from . import views

app_name = 'authentication'

urlpatterns = [
    # Login
    url(r'^login', views.auth_login, name='login'),
    # Logout
    url(r'^logout', views.auth_logout, name='logout'),
    # Register
    url(r'^confirm/(?P<confirmation>[^/]+)', views.auth_register),
    url(r'^register', views.auth_register),
    # Change Password
    url(r'^account/manage/change-password', views.auth_change_password),
    # Forgot Password
    url(r'^forgot-password', views.auth_forgot_password),
    url(r'^reset-password/(?P<reset_confirmation>[^/]+)', views.auth_reset_password),
    url(r'^reset-password', views.auth_reset_password),
    # Account Manage
    url(r'^account/manage', views.auth_manage),
    # Notifications
    url(r'^notifications/seen', views.notification_seen),
]
