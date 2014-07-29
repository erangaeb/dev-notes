from django.conf.urls.defaults import url
from tastypie import http
from tastypie.exceptions import ImmediateHttpResponse
from tastypie.resources import ModelResource
from tastypie.authentication import BasicAuthentication
from tastypie.authorization import DjangoAuthorization
from tastypie.utils import trailing_slash
from datetime import datetime
from django.utils.timezone import utc
from gcm.models import Device

import logging
import json


logger = logging.getLogger(__name__)


class DeviceResource(ModelResource):
    """
    Resource class to work with GCM Devices

    This gives API to interact with Device model, allow to get, create and
    update devices,
        get - get registered devices of given user
        create - registering device for GCM
        update - un-register device from GCM(update is_active to False)
    Not allow DELETE requests here
    """
    class Meta:
        queryset = Device.objects.all().distinct().order_by('-created_date')
        resource_name = 'devices'
        authentication = BasicAuthentication()
        authorization = DjangoAuthorization()
        include_resource_uri = False
        allowed_methods = ['get', 'post', 'put']

    def override_urls(self):
        """
        Add capability to filter Device  with its device_id. In here we need to
        return only one device object wich matches to given device_id. There
        should be only one device with a given device_id for a user

        Add url entry that maps to 'devices/?device_id=<param>'. When request
        comes to this url 'get_device' method will be call
        """
        return [
            url(r"^(?P<resource_name>%s)/%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('get_device'),
                name="api_get_device"
            ),
        ]

    def get_device(self, request, **kwargs):
        """
        When request comes with 'devices/?url<param>' url this method
        will be invoke, need to extract Device object which matches to
        arg 'device_id'

        There should be only one Device object should be retun with response,
        since device_id field is unique in Device.
        """
        # need to check the
        #   1. request type
        #   2. user authentication
        self.method_check(request, allowed=['get'])
        self.is_authenticated(request)
        self.throttle_check(request)

        device_id = request.GET['device_id']
        device = None
        logger.debug("get_device, with device_id %s" % (device_id))

        try:
            device = Device.objects.get(device_id=device_id, user=request.user)
        except Device.DoesNotExist:
            logger.debug("get_device, Device does not exists")

            msg = json.dumps({"error": "no device"})
            raise ImmediateHttpResponse(response=http.HttpNotFound(msg))

        # get device details via calling Resouce method 'get_details'
        device_resource = DeviceResource()
        return device_resource.get_detail(request, pk=device.pk)

    def dehydrate(self, bundle):
        """
        Need to dehydrate response objects and remove reg_id from response,
        since we don't expose reg id to user
        """
        # delete reg_id from response
        del bundle.data['reg_id']
        return bundle

    def obj_create(self, bundle, request=None, **kwargs):
        """
        Override this method in order to avoid to do unauthorized things, get
        user from request and set it as device user
        """
        # set modified date to none since no update yet
        bundle.data['created_date'] = datetime.utcnow().replace(tzinfo=utc)
        logger.debug("obj_create: %s " % (bundle.data))
        return super(DeviceResource, self).obj_create(bundle, request,
                                                      user=request.user)

    def obj_update(self, bundle, request=None, **kwargs):
        """
        Override update to avoid unauthorized things to happen. We need to set
        change modified date here
        """
        bundle.data['modified_date'] = datetime.utcnow().replace(tzinfo=utc)
        logger.debug("obj_update: %s " % (bundle.data))
        return super(DeviceResource, self).obj_update(bundle, request,
                                                      **kwargs)

    def apply_authorization_limits(self, request, object_list):
        """
        Filter only own devices of request user, some part of authorization
        handles in here as well
        """
        return object_list.filter(user=request.user)
