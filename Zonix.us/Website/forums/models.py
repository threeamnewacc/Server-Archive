import datetime
import uuid

from django.db import models
from django.contrib import admin

from precise_bbcode.fields import BBCodeTextField


class Category(models.Model):
    category_id = models.AutoField(primary_key=True)
    category_title = models.CharField(max_length=200)
    category_order = models.IntegerField(default=0)


class Forum(models.Model):
    category_id = models.IntegerField(null=False)
    forum_id = models.AutoField(primary_key=True)
    forum_title = models.CharField(max_length=200)
    forum_description = models.CharField(max_length=200)
    forum_order = models.IntegerField(default=0)
    requires_admin = models.BooleanField(default=False)


class Thread(models.Model):
    forum_id = models.IntegerField(null=False)
    thread_id = models.AutoField(primary_key=True)
    thread_title = models.CharField(max_length=200)
    thread_content = BBCodeTextField(max_length=8192)
    thread_creator = models.UUIDField(null=False, max_length=36, default=uuid.uuid4())
    publish_date = models.DateTimeField(
        'date published',
        default=datetime.datetime.now
    )
    updated_date = models.DateTimeField(
        'date updated',
        default=datetime.datetime.now
    )
    bumped_date = models.DateTimeField(
        'date bumped',
        default=datetime.datetime.now
    )
    hidden = models.BooleanField(default=False)
    locked = models.BooleanField(default=False)
    stickied = models.BooleanField(default=False)
    announcement = models.BooleanField(default=False)

    def get_last_reply(self):
        return Reply.objects.filter(thread_id=self.thread_id).order_by('-publish_date').first()


class Reply(models.Model):
    thread_id = models.IntegerField(null=False)
    reply_id = models.AutoField(primary_key=True)
    reply_content = BBCodeTextField(max_length=8192)
    reply_creator = models.UUIDField(null=False, max_length=36, default=uuid.uuid4())
    publish_date = models.DateTimeField(
        'date published',
        default=datetime.datetime.now
    )


class PostLike(models.Model):
    post_id = models.TextField(null=False)
    reply_creator = models.UUIDField(null=False, max_length=36, default=uuid.uuid4())
    created_date = models.DateTimeField(
        'date liked',
        default=datetime.datetime.now
    )


admin.site.register(Category)
admin.site.register(Forum)
admin.site.register(Thread)
admin.site.register(Reply)
