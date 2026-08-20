package com.ami.service;

import com.ami.entity.Device;
import com.ami.entity.User;
import com.ami.enums.DeviceLocationSource;

public interface LocationService {

    void saveOrUpdateUserLocation(User user);

    void saveOrUpdateDeviceLocation(Device device, DeviceLocationSource source);

    void saveOrUpdateDeviceLocationFromUser(Device device, User user);

    void updateAssignedDeviceLocationsForUser(User user);
    
    void backfillDeviceLocations();

    void backfillUserLocations();
}