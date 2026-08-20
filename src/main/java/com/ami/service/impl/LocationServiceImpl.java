package com.ami.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.entity.Device;
import com.ami.entity.DeviceLocation;
import com.ami.entity.User;
import com.ami.entity.UserLocation;
import com.ami.enums.DeviceLocationSource;
import com.ami.repository.DeviceLocationRepository;
import com.ami.repository.DeviceRepository;
import com.ami.repository.UserLocationRepository;
import com.ami.repository.UserRepository;
import com.ami.service.GeoCodingService;
import com.ami.service.LocationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

	private final UserLocationRepository userLocationRepository;
	private final DeviceLocationRepository deviceLocationRepository;
	private final DeviceRepository deviceRepository;
	private final GeoCodingService geoCodingService;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void saveOrUpdateUserLocation(User user) {

		if (!hasAnyLocation(user.getAddress(), user.getCity(), user.getState())) {
			userLocationRepository.deleteByUser(user);
			return;
		}

		String locationText = buildLocation(user.getAddress(), user.getCity(), user.getState());

		GeoCodingService.GeoLocation geoLocation = geoCodingService.getCoordinates(locationText);

		if (geoLocation == null) {
			System.out.println("Location not found for user: " + user.getEmail() + ". Location: " + locationText);
			return;
		}

		UserLocation userLocation = userLocationRepository.findByUser(user)
				.orElse(UserLocation.builder().user(user).build());

		userLocation.setAddress(user.getAddress());
		userLocation.setCity(user.getCity());
		userLocation.setState(user.getState());
		userLocation.setCountry("India");

		userLocation.setLatitude(geoLocation.latitude());
		userLocation.setLongitude(geoLocation.longitude());

		userLocationRepository.save(userLocation);
	}

	@Override
	@Transactional
	public void saveOrUpdateDeviceLocation(Device device, DeviceLocationSource source) {

		if (!hasAnyLocation(device.getCustomerAddress(), device.getCity(), device.getState())) {
			deviceLocationRepository.deleteByDevice(device);
			return;
		}

		String locationText = buildLocation(device.getCustomerAddress(), device.getCity(), device.getState());

		GeoCodingService.GeoLocation geoLocation = geoCodingService.getCoordinates(locationText);

		if (geoLocation == null) {
			System.out
					.println("Location not found for device: " + device.getDeviceId() + ". Location: " + locationText);
			return;
		}

		DeviceLocation deviceLocation = deviceLocationRepository.findByDevice(device)
				.orElse(DeviceLocation.builder().device(device).build());

		deviceLocation.setAddress(device.getCustomerAddress());
		deviceLocation.setCity(device.getCity());
		deviceLocation.setState(device.getState());
		deviceLocation.setCountry("India");
		deviceLocation.setLocationSource(source);

		deviceLocation.setLatitude(geoLocation.latitude());
		deviceLocation.setLongitude(geoLocation.longitude());

		deviceLocationRepository.save(deviceLocation);
	}

	@Override
	@Transactional
	public void saveOrUpdateDeviceLocationFromUser(Device device, User user) {

		if (!hasAnyLocation(user.getAddress(), user.getCity(), user.getState())) {
			deviceLocationRepository.deleteByDevice(device);
			return;
		}

		String locationText = buildLocation(user.getAddress(), user.getCity(), user.getState());

		GeoCodingService.GeoLocation geoLocation = geoCodingService.getCoordinates(locationText);

		if (geoLocation == null) {
			System.out
					.println("Location not found for device: " + device.getDeviceId() + ". Location: " + locationText);
			return;
		}

		DeviceLocation deviceLocation = deviceLocationRepository.findByDevice(device)
				.orElse(DeviceLocation.builder().device(device).build());

		deviceLocation.setAddress(user.getAddress());
		deviceLocation.setCity(user.getCity());
		deviceLocation.setState(user.getState());
		deviceLocation.setCountry("India");
		deviceLocation.setLocationSource(DeviceLocationSource.ASSIGNED_USER);

		deviceLocation.setLatitude(geoLocation.latitude());
		deviceLocation.setLongitude(geoLocation.longitude());

		deviceLocationRepository.save(deviceLocation);
	}

	@Override
	@Transactional
	public void updateAssignedDeviceLocationsForUser(User user) {

		List<Device> assignedDevices = deviceRepository.findByAssignedUserId(user.getId());

		for (Device device : assignedDevices) {
			saveOrUpdateDeviceLocationFromUser(device, user);
		}
	}

	private boolean hasAnyLocation(String address, String city, String state) {
		return isPresent(address) || isPresent(city) || isPresent(state);
	}

	private boolean isPresent(String value) {
		return value != null && !value.isBlank();
	}

	private String buildLocation(String address, String city, String state) {

		List<String> parts = new ArrayList<>();

		addUnique(parts, address);
		addUnique(parts, city);
		addUnique(parts, state);
		addUnique(parts, "India");

		return String.join(", ", parts);
	}

	private void addUnique(List<String> parts, String value) {

		if (value == null || value.isBlank()) {
			return;
		}

		String cleaned = value.trim();

		boolean alreadyExists = parts.stream().anyMatch(existing -> existing.equalsIgnoreCase(cleaned));

		if (!alreadyExists) {
			parts.add(cleaned);
		}
	}

	@Override
	public void backfillDeviceLocations() {

		List<Device> devices = deviceRepository.findAll();

		for (Device device : devices) {

			if (device.getMeter() == null) {
				continue;
			}

			DeviceLocation existingLocation = deviceLocationRepository.findByDevice(device).orElse(null);

			if (existingLocation != null && existingLocation.getLatitude() != null
					&& existingLocation.getLongitude() != null) {
				continue;
			}

			try {
				saveOrUpdateDeviceLocation(device, DeviceLocationSource.DEVICE_CREATE);
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
			
			sleepBetweenGeoCalls();
		}
	}

	@Override
	public void backfillUserLocations() {

		List<User> users = userRepository.findAll();

		for (User user : users) {

			UserLocation existingLocation = userLocationRepository.findByUser(user).orElse(null);

			if (existingLocation != null && existingLocation.getLatitude() != null
					&& existingLocation.getLongitude() != null) {
				continue;
			}

			try {
				saveOrUpdateUserLocation(user);
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
			
			sleepBetweenGeoCalls();
		}
	}

	private void sleepBetweenGeoCalls() {
		try {
			Thread.sleep(1200);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}