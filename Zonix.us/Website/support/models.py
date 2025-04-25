import datetime
import random

from django.db import models
from django.contrib.auth.models import AnonymousUser

from precise_bbcode.fields import BBCodeTextField

from authentication.models import SiteUser, SiteNotification
from data.models import DataHelper


class Ticket(models.Model):
    TICKET_TYPES = (
        ('a', 'Punishment Appeal'),
        ('b', 'Report Player'),
        ('c', 'Store Issues / Questions'),
    )
    TICKET_STATUSES = (
        ('a', 'Open'),
        ('b', 'Solved'),
        ('c', 'Closed'),
        ('d', 'Creator Replied'),
        ('e', 'Operator Replied'),
    )

    ticket_id = models.AutoField(primary_key=True)
    ticket_title = models.CharField(max_length=200)
    ticket_content = BBCodeTextField()
    ticket_creator = models.UUIDField(null=False)
    ticket_status = models.CharField(max_length=1, choices=TICKET_STATUSES)
    ticket_type = models.CharField(max_length=1, choices=TICKET_TYPES)
    ticket_handler = models.UUIDField(null=True)
    publish_date = models.DateTimeField(
        'date published',
        default=datetime.datetime.now()
    )
    updated_date = models.DateTimeField(
        'date updated',
        default=datetime.datetime.now()
    )

    def get_pretty_type(self):
        return Ticket.TICKET_TYPES[Ticket.convert_to_num(self.ticket_type)][1]

    def get_pretty_status(self):
        return Ticket.TICKET_STATUSES[Ticket.convert_to_num(self.ticket_status)][1]

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
        else:
            return 4

    def can_user_access(self, user):
        if user is None:
            return False

        if isinstance(user, AnonymousUser):
            return False

        if self.ticket_creator == user.uid or (user.is_staff and self.ticket_handler is not None and self.ticket_handler == user.uid) or (user.is_staff and user.is_admin):
            return True

        return False

    def assign_handler(self):
        staff_data = DataHelper.get_staff_data()

        types = []

        if self.ticket_type == 'a':
            types = ['SENIOR_MODERATOR', 'ADMINISTRATOR', 'MANAGER', 'DEVELOPER', 'OWNER']
        elif self.ticket_type == 'b':
            types = ['TRIAL_MODERATOR', 'MODERATOR', 'SENIOR_MODERATOR', 'ADMINISTRATOR', 'MANAGER', 'DEVELOPER', 'OWNER']
        elif self.ticket_type == 'c':
            types = ['MANAGER', 'DEVELOPER', 'OWNER']

        sorted_players = []

        for staff in staff_data:
            if staff in types:
                for player in staff_data[staff]:
                    sorted_players.append(player)

        # add random shuffling
        random.shuffle(sorted_players, random.random)

        leading_uuid = None
        leading_count = 0

        for entry in sorted_players:
            blacklist_entry = TicketBlacklistEntry.objects.filter(uid=entry).first()

            if blacklist_entry is not None:
                continue

            user = SiteUser.objects.filter(uid=entry).first()

            if user is None:
                continue

            tickets = Ticket.objects.filter(ticket_handler=entry, ticket_status__in=['a', 'b', 'd', 'e'])
            tickets_len = len(tickets)

            if leading_uuid is None or tickets_len < leading_count:
                leading_uuid = entry
                leading_count = tickets_len

        if leading_uuid is None:
            return None

        user = SiteUser.objects.filter(uid=leading_uuid).first()

        notification = SiteNotification()
        notification.user = user.uid
        notification.type = 'TICKETS_ASSIGNED'
        notification.data = {
            'ticket': {
                'id': self.ticket_id,
                'title': self.ticket_title
            }
        }
        notification.save()

        self.ticket_handler = leading_uuid
        self.save()


class TicketReply(models.Model):
    ticket_id = models.IntegerField(null=False)
    reply_id = models.AutoField(primary_key=True)
    reply_content = BBCodeTextField()
    reply_creator = models.UUIDField(null=False)
    publish_date = models.DateTimeField(
        'date published',
        default=datetime.datetime.now()
    )


class TicketBlacklistEntry(models.Model):
    uid = models.UUIDField(primary_key=True, null=False)
