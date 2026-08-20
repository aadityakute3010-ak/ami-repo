package com.ami.service;

public interface GeoCodingService {

    GeoLocation getCoordinates(String locationText);

    record GeoLocation(Double latitude, Double longitude) {}
}