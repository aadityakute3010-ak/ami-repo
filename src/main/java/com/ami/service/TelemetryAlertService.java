package com.ami.service;

import com.ami.entity.DeviceTelemetry;


public interface TelemetryAlertService {

    void checkTelemetryAlerts(
            DeviceTelemetry telemetry);

   
}