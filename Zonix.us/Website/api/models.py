from django.contrib import admin
from django.db import models


class ClientWhitelist(models.Model):
    id = models.AutoField(primary_key=True)
    ip_address = models.TextField(null=False, blank=False, unique=True)


admin.site.register(ClientWhitelist)
