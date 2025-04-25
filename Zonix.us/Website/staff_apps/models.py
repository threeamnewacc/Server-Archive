import datetime

from django.db import models


class StaffApplication(models.Model):
    STATUSES = (
        ('a', 'Under Review'),
        ('b', 'Awaiting Interview'),
        ('c', 'Accepted'),
        ('d', 'Denied'),
    )

    REGIONS = (
        ('a', 'NA'),
        ('b', 'EU'),
        ('c', 'SA'),
        ('d', 'AS'),
        ('e', 'Other(s)'),
    )

    ACTIVITY = (
        ('a', 'A couple hours every other day'),
        ('b', '1-2 hours a day'),
        ('c', '2-4 hours a day'),
        ('d', '4-8 hours a day'),
    )

    id = models.AutoField(primary_key=True)
    creator = models.UUIDField(null=False)
    first_name = models.TextField(blank=False, null=False, default='', max_length=40)
    telegram = models.TextField(blank=False, null=False, default='', max_length=48)
    previous_experience = models.TextField(blank=False, null=False, default='', max_length=4096)
    about_yourself = models.TextField(blank=False, null=False, default='', max_length=4096)
    cheating_opinion = models.TextField(blank=False, null=False, default='', max_length=4096)
    status = models.CharField(max_length=1, choices=STATUSES, default='a')
    region = models.CharField(max_length=1, choices=REGIONS, default='a')
    activity = models.CharField(max_length=1, choices=ACTIVITY, default='a')
    languages_english = models.BooleanField(null=False, default=False)
    languages_spanish = models.BooleanField(null=False, default=False)
    languages_german = models.BooleanField(null=False, default=False)
    languages_french = models.BooleanField(null=False, default=False)
    languages_other = models.BooleanField(null=False, default=False)
    publish_date = models.DateTimeField(
        'date published',
        default=datetime.datetime.now()
    )
    updated_date = models.DateTimeField(
        'date updated',
        default=datetime.datetime.now()
    )

    def get_pretty_status(self):
        return StaffApplication.STATUSES[StaffApplication.convert_to_num(self.status)][1]

    def get_pretty_region(self):
        return StaffApplication.REGIONS[StaffApplication.convert_to_num(self.region)][1]

    def get_pretty_activity(self):
        return StaffApplication.ACTIVITY[StaffApplication.convert_to_num(self.activity)][1]

    @staticmethod
    def convert_to_num(char):
        if char == 'a':
            return 0
        elif char == 'b':
            return 1
        elif char == 'c':
            return 2
        elif char == 'd':
            return 3
        elif char == 'e':
            return 4
        elif char == 'f':
            return 5
        else:
            return 6
