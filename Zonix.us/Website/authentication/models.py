import datetime
import uuid

from django.contrib.auth.models import BaseUserManager, AbstractBaseUser
from django.core.cache import cache
from django.db import models

from data.models import get_name_from_id, get_rank_from_id
from ipware.ip import get_ip
from jsonfield import JSONField


rank_permissions = {
    'OWNER': ['*'],
    'DEVELOPER': ['*'],
    'MANAGER': ['*'],
    'PLATFORM_ADMINISTRATOR': ['*'],
    'ADMINISTRATOR': ['*'],
    'SENIOR_MODERATOR': [
        'STAFF',
        'VIEW_AC_LOGS'
    ],
    'MODERATOR': ['STAFF'],
    'TRIAL_MOD': ['STAFF'],
    'PARTNER': ['CLIENT'],
    'FAMOUS': ['CLIENT'],
    'MEDIA': ['CLIENT'],
    'ZONIX': ['CLIENT']
}


class Authentication:
    @staticmethod
    def is_cooldown(user):
        if cache.get(user.uid.__str__() + '_cooldown'):
            return True

        cache.set(user.uid.__str__() + '_cooldown', True, 5)

        return False

    @staticmethod
    def authenticate(email, password):
        if email is None:
            return None

        if password is None:
            return None

        users = SiteUser.objects.filter(email=email)

        if users is None or len(users) == 0:
            return None

        user = users[0]

        if user is None:
            return None

        if user.check_password(password):
            return user
        else:
            return None


class SiteUserManager(BaseUserManager):
    def create_user(self, email, uid, password=None):
        if not email:
            raise ValueError('Users must have an email address')

        if not uid:
            raise ValueError('Users must have a uid')

        user = self.model(
            email=self.normalize_email(email),
            uid=uid,
        )

        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_superuser(self, email, uid, password):
        user = self.create_user(
            email,
            password=password,
            uid=uid,
        )
        user.save(using=self._db)
        return user


class SiteUser(AbstractBaseUser):
    email = models.EmailField(
        verbose_name='email address',
        max_length=255,
        unique=True,
    )
    uid = models.UUIDField(default=uuid.uuid4())
    join_date = models.DateTimeField(
        'date joined',
        default=datetime.datetime.now()
    )
    is_active = models.BooleanField(default=True)
    youtube_link = models.URLField(blank=True, null=True, default=None)
    twitter_link = models.URLField(blank=True, null=True, default=None)
    reset_confirmation = models.TextField(blank=True, null=True)

    objects = SiteUserManager()

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['uid']

    def get_username(self):
        return get_name_from_id(self.uid.__str__())

    def get_rank(self):
        return get_rank_from_id(self.uid.__str__())

    def get_uuid(self):
        return self.uid.__str__()

    def get_full_name(self):
        # The user is identified by their uid
        return self.uid

    def get_short_name(self):
        # The user is identified by their uid
        return self.uid

    def __str__(self):
        return self.uid.__str__()

    def has_perm(self, perm, obj=None):
        if self.get_rank() not in rank_permissions:
            return False

        if '*' in rank_permissions[self.get_rank()]:
            return True

        return perm in rank_permissions[self.get_rank()]

    def has_module_perms(self, app_label):
        return True

    @property
    def is_admin(self):
        return self.has_perm('*')

    @property
    def is_staff(self):
        return self.has_perm('STAFF')
    
    @property
    def can_view_logs(self):
        return self.has_perm('VIEW_AC_LOGS')

    @property
    def can_client_whitelist(self):
        return self.is_admin | self.is_staff or self.has_perm('CLIENT')

    def insert_log(self, request, log):
        if log is None:
            raise ValueError('Log cannot be None')

        new_log = SiteUserLog()
        new_log.user = self.uid
        new_log.ip_address = get_ip(request)
        new_log.log = log
        new_log.save()


class SiteUserBan(models.Model):
    reason = models.CharField(max_length=400)
    expiration = models.DateTimeField(
        'date expired',
        default=datetime.datetime.now()
    )
    active = models.BooleanField


class SiteUserLog(models.Model):
    user = models.UUIDField(default=uuid.uuid4())
    log = models.TextField()
    ip_address = models.TextField()
    datetime = models.DateTimeField(
        'log created',
        default=datetime.datetime.now()
    )


class SiteNotification(models.Model):
    id = models.AutoField(primary_key=True)
    user = models.UUIDField(null=False)
    type = models.TextField(blank=False, null=False)
    data = JSONField(default=None)
    seen = models.BooleanField(default=False)
    datetime = models.DateTimeField(
        'notification created',
        default=datetime.datetime.now()
    )
