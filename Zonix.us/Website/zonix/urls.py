"""zonix URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.conf.urls import url, include

urlpatterns = [
    url(r'^', include('api.urls')),
    url(r'^', include('authentication.urls')),
    url(r'^', include('base.urls')),
    url(r'^admin/', include('admin_panel.urls')),
    url(r'^forums/', include('forums.urls')),
    url(r'^support/', include('support.urls')),
    url(r'^applications/', include('staff_apps.urls')),
]
