from django.db import models
from django.contrib.auth.models import User


class Device(models.Model):
    """
    Model class to keep google could messaging enable device details

    Need to keep device registration id as well as active state. There's a
    REST api to deal with these devices. API doing registering and
    un-registering devices

    When registering create a Device with is_active True,
    when un-registering update value of is_active to False
    """
    name = models.CharField(max_length=64, null=True, blank=True)
    device_id = models.CharField(max_length=64, unique=True)
    reg_id = models.TextField()
    created_date = models.DateTimeField()
    modified_date = models.DateTimeField(null=True, blank=True)
    is_active = models.BooleanField(default=False)
    user = models.ForeignKey(User)
