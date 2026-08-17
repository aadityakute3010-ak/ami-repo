package com.ami.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ami.entity.Alert;
import com.ami.entity.DeviceTelemetry;
import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;
import com.ami.enums.AlertStatus;
import com.ami.repository.AlertRepository;
import com.ami.service.NotificationService;
import com.ami.entity.DeviceTelemetry;
import com.ami.service.TelemetryAlertService;
import com.ami.entity.AlertHistory;
import com.ami.repository.AlertHistoryRepository;
@Service
public class TelemetryAlertServiceImpl
        implements TelemetryAlertService {
	
	private final AlertRepository alertRepository;

	private final NotificationService notificationService;
	
	private final AlertHistoryRepository alertHistoryRepository;
	
	public TelemetryAlertServiceImpl(

	        AlertRepository alertRepository,

	        AlertHistoryRepository alertHistoryRepository,

	        NotificationService notificationService) {

	    this.alertRepository = alertRepository;

	    this.notificationService = notificationService;
	    
	    this.alertHistoryRepository =
	            alertHistoryRepository;
	}
	@Override
	@Transactional
	public void checkTelemetryAlerts(
	        DeviceTelemetry telemetry) {

	    switch (telemetry.getSourceType()) {

	        case WATER ->
	                checkWaterAlerts(telemetry);

	        case GAS ->
	                checkGasAlerts(telemetry);

	        case ENERGY ->
	                checkEnergyAlerts(telemetry);

	        case SOLAR ->
	                checkSolarAlerts(telemetry);

	        default -> {
	        }
	    }
	}
	
	
	
	private void checkWaterAlerts(
	        DeviceTelemetry telemetry) {

	    // Leak Detection
		if (Boolean.TRUE.equals(
		        telemetry.getLeakDetected())) {

		    createAlert(
		            telemetry,
		            "Water Leak",
		            "Leak detected for device "
		                    + telemetry.getDeviceId(),
		            AlertSeverity.CRITICAL,
		            AlertCategory.LEAK);

		} else {

		    resolveAlert(
		            telemetry,
		            "Water Leak");
		}

	    // Critical Tank Level
		if (telemetry.getConsumption() != null
		        && telemetry.getConsumption() <= 10) {

		    createAlert(
		            telemetry,
		            "Low Tank Level",
		            "Tank level is below 10%",
		            AlertSeverity.CRITICAL,
		            AlertCategory.TANK);

		} else {

		    resolveAlert(
		            telemetry,
		            "Low Tank Level");
		}

	    // Low Pressure
		if (telemetry.getPressure() != null
		        && telemetry.getPressure() < 20) {

		    createAlert(
		            telemetry,
		            "Low Water Pressure",
		            "Water pressure is below threshold",
		            AlertSeverity.HIGH,
		            AlertCategory.PRESSURE);

		} else {

		    resolveAlert(
		            telemetry,
		            "Low Water Pressure");
		}
	    // Low Battery
		if (telemetry.getBatteryLevel() != null
		        && telemetry.getBatteryLevel() < 20) {

		    createAlert(
		            telemetry,
		            "Low Battery",
		            "Battery level below 20%",
		            AlertSeverity.WARNING,
		            AlertCategory.BATTERY);

		} else {

		    resolveAlert(
		            telemetry,
		            "Low Battery");
		}

	    // Device Offline
		if (Boolean.FALSE.equals(
		        telemetry.getDeviceOnline())) {

		    createAlert(
		            telemetry,
		            "Device Offline",
		            "Water device is offline",
		            AlertSeverity.HIGH,
		            AlertCategory.COMMUNICATION);

		} else {

		    resolveAlert(
		            telemetry,
		            "Device Offline");
		}
	}
	private void checkGasAlerts(
	        DeviceTelemetry telemetry) {

	    // Gas Leak
	    if (Boolean.TRUE.equals(
	            telemetry.getLeakDetected())) {

	        createAlert(

	                telemetry,

	                "Gas Leak",

	                "Gas leak detected",

	                AlertSeverity.CRITICAL,

	                AlertCategory.LEAK);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Gas Leak");
	    }

	    // Low Pressure
	    if (telemetry.getPressure() != null
	            && telemetry.getPressure() < 15) {

	        createAlert(

	                telemetry,

	                "Low Gas Pressure",

	                "Gas pressure below threshold",

	                AlertSeverity.HIGH,

	                AlertCategory.PRESSURE);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Low Gas Pressure");
	    }

	    // High Pressure
	    if (telemetry.getPressure() != null
	            && telemetry.getPressure() > 80) {

	        createAlert(

	                telemetry,

	                "High Gas Pressure",

	                "Gas pressure above threshold",

	                AlertSeverity.CRITICAL,

	                AlertCategory.PRESSURE);

	    } else {

	        resolveAlert(

	                telemetry,

	                "High Gas Pressure");
	    }

	    // Battery
	    if (telemetry.getBatteryLevel() != null
	            && telemetry.getBatteryLevel() < 20) {

	        createAlert(

	                telemetry,

	                "Low Battery",

	                "Gas meter battery low",

	                AlertSeverity.WARNING,

	                AlertCategory.BATTERY);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Low Battery");
	    }

	    // Device Offline
	    if (Boolean.FALSE.equals(
	            telemetry.getDeviceOnline())) {

	        createAlert(

	                telemetry,

	                "Gas Meter Offline",

	                "Gas meter offline",

	                AlertSeverity.HIGH,

	                AlertCategory.COMMUNICATION);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Gas Meter Offline");
	    }

	    // Tamper
	    if (Boolean.TRUE.equals(
	            telemetry.getTamperDetected())) {

	        createAlert(

	                telemetry,

	                "Gas Meter Tampered",

	                "Possible tampering detected",

	                AlertSeverity.CRITICAL,

	                AlertCategory.TAMPER);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Gas Meter Tampered");
	    }
	    
	 // Emergency Shutdown
	    if (Boolean.TRUE.equals(
	            telemetry.getEmergencyShutdown())) {

	        createAlert(

	                telemetry,

	                "Emergency Shutdown",

	                "Emergency shutdown activated",

	                AlertSeverity.CRITICAL,

	                AlertCategory.SYSTEM);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Emergency Shutdown");
	    }
	 // Alarm Active
	    if (Boolean.TRUE.equals(
	            telemetry.getAlarmActive())) {

	        createAlert(

	                telemetry,

	                "Gas Alarm Active",

	                "Gas alarm has been activated",

	                AlertSeverity.HIGH,

	                AlertCategory.SYSTEM);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Gas Alarm Active");
	    }
	    
	 // High Gas Concentration
	    if (telemetry.getGasConcentration() != null
	            && telemetry.getGasConcentration() > 80) {

	        createAlert(

	                telemetry,

	                "High Gas Concentration",

	                "Gas concentration exceeds safe threshold",

	                AlertSeverity.CRITICAL,

	                AlertCategory.SYSTEM);

	    } else {

	        resolveAlert(

	                telemetry,

	                "High Gas Concentration");
	    }
	 // High Gas Density
	    if (telemetry.getGasDensity() != null
	            && telemetry.getGasDensity() > 1.2) {

	        createAlert(

	                telemetry,

	                "High Gas Density",

	                "Gas density exceeds safe threshold",

	                AlertSeverity.HIGH,

	                AlertCategory.SYSTEM);

	    } else {

	        resolveAlert(

	                telemetry,

	                "High Gas Density");
	    }
	 // Poor Gas Quality
	    if (telemetry.getGasQuality() != null
	            && !telemetry.getGasQuality().equalsIgnoreCase("GOOD")) {

	        createAlert(

	                telemetry,

	                "Poor Gas Quality",

	                "Gas quality is below acceptable level",

	                AlertSeverity.HIGH,

	                AlertCategory.SYSTEM);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Poor Gas Quality");
	    }// Poor Gas Quality
	    if (telemetry.getGasQuality() != null
	            && !telemetry.getGasQuality().equalsIgnoreCase("GOOD")) {

	        createAlert(

	                telemetry,

	                "Poor Gas Quality",

	                "Gas quality is below acceptable level",

	                AlertSeverity.HIGH,

	                AlertCategory.SYSTEM);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Poor Gas Quality");
	    }
	}
	
	private void checkEnergyAlerts(
	        DeviceTelemetry telemetry) {

	    // High Voltage
	    if (telemetry.getVoltage() != null
	            && telemetry.getVoltage() > 250) {

	        createAlert(

	                telemetry,

	                "High Voltage",

	                "Voltage exceeds safe limit",

	                AlertSeverity.CRITICAL,

	                AlertCategory.VOLTAGE);

	    } else {

	        resolveAlert(

	                telemetry,

	                "High Voltage");
	    }

	    // Low Voltage
	    if (telemetry.getVoltage() != null
	            && telemetry.getVoltage() < 180) {

	        createAlert(

	                telemetry,

	                "Low Voltage",

	                "Voltage below safe limit",

	                AlertSeverity.HIGH,

	                AlertCategory.VOLTAGE);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Low Voltage");
	    }

	    // High Current
	    if (telemetry.getCurrent() != null
	            && telemetry.getCurrent() > 100) {

	        createAlert(

	                telemetry,

	                "High Current",

	                "Current exceeds threshold",

	                AlertSeverity.CRITICAL,

	                AlertCategory.CURRENT);

	    } else {

	        resolveAlert(

	                telemetry,

	                "High Current");
	    }

	    // Tamper
	    if (Boolean.TRUE.equals(
	            telemetry.getTamperDetected())) {

	        createAlert(

	                telemetry,

	                "Meter Tampered",

	                "Possible meter tampering detected",

	                AlertSeverity.CRITICAL,

	                AlertCategory.TAMPER);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Meter Tampered");
	    }

	    // Battery
	    if (telemetry.getBatteryLevel() != null
	            && telemetry.getBatteryLevel() < 20) {

	        createAlert(

	                telemetry,

	                "Low Battery",

	                "Battery level below threshold",

	                AlertSeverity.WARNING,

	                AlertCategory.BATTERY);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Low Battery");
	    }

	    // Device Offline
	    if (Boolean.FALSE.equals(
	            telemetry.getDeviceOnline())) {

	        createAlert(

	                telemetry,

	                "Energy Meter Offline",

	                "Energy meter is offline",

	                AlertSeverity.HIGH,

	                AlertCategory.COMMUNICATION);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Energy Meter Offline");
	    }
	}
	private void checkSolarAlerts(
	        DeviceTelemetry telemetry) {

	    // Solar Panel Offline
	    if (Boolean.FALSE.equals(
	            telemetry.getDeviceOnline())) {

	        createAlert(

	                telemetry,

	                "Solar Panel Offline",

	                "Solar panel is offline",

	                AlertSeverity.HIGH,

	                AlertCategory.COMMUNICATION);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Solar Panel Offline");
	    }

	    // Low Battery
	    if (telemetry.getBatteryLevel() != null
	            && telemetry.getBatteryLevel() < 20) {

	        createAlert(

	                telemetry,

	                "Low Battery",

	                "Solar battery level below threshold",

	                AlertSeverity.WARNING,

	                AlertCategory.BATTERY);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Low Battery");
	    }

	    // High Temperature
	    if (telemetry.getTemperature() != null
	            && telemetry.getTemperature() > 70) {

	        createAlert(

	                telemetry,

	                "High Panel Temperature",

	                "Solar panel temperature is too high",

	                AlertSeverity.HIGH,

	                AlertCategory.TEMPERATURE);

	    } else {

	        resolveAlert(

	                telemetry,

	                "High Panel Temperature");
	    }

	    // Low Voltage
	    if (telemetry.getVoltage() != null
	            && telemetry.getVoltage() < 18) {

	        createAlert(

	                telemetry,

	                "Low Solar Voltage",

	                "Solar voltage below threshold",

	                AlertSeverity.WARNING,

	                AlertCategory.VOLTAGE);

	    } else {

	        resolveAlert(

	                telemetry,

	                "Low Solar Voltage");
	    }

	    // High Current
	    if (telemetry.getCurrent() != null
	            && telemetry.getCurrent() > 50) {

	        createAlert(

	                telemetry,

	                "High Solar Current",

	                "Solar current exceeds threshold",

	                AlertSeverity.HIGH,

	                AlertCategory.CURRENT);

	    } else {

	        resolveAlert(

	                telemetry,

	                "High Solar Current");
	    }
	}
	private void createAlert(

	        DeviceTelemetry telemetry,

	        String name,

	        String message,

	        AlertSeverity severity,

	        AlertCategory category) {

	    Optional<Alert> existingAlert =

	            alertRepository.findByDeviceIdAndNameAndStatus(

	                    telemetry.getDeviceId(),

	                    name,

	                    AlertStatus.ACTIVE);

	    if (existingAlert.isPresent()) {
	        return;
	    }

	    Alert alert =

	            Alert.builder()

	                    .name(name)

	                    .deviceId(
	                            telemetry.getDeviceId())

	                    .message(message)

	                    .severity(severity)

	                    .category(category)

	                    .source(
	                            AlertSource.valueOf(
	                                    telemetry.getSourceType().name()))

	                    .status(
	                            AlertStatus.ACTIVE)

	                    .enabled(true)

	                    .createdAt(
	                            java.time.LocalDateTime.now())

	                    .build();

	    alert = alertRepository.save(alert);
	    
	    AlertHistory history =
	            AlertHistory.builder()
	                    .alertId(
	                            alert.getId())
	                    .action(
	                            "AUTO_CREATED")
	                    .description(
	                            "Alert generated automatically from telemetry.")
	                    .timestamp(
	                            java.time.LocalDateTime.now())
	                    .build();

	    alertHistoryRepository.save(
	            history);

	    notificationService.alertCreated(

	            alert.getId(),

	            alert.getName(),

	            alert.getSeverity().name(),

	            alert.getMessage());
	}
	private void resolveAlert(

	        DeviceTelemetry telemetry,

	        String alertName) {

	    List<Alert> alerts =

	            alertRepository.findByDeviceIdAndStatus(

	                    telemetry.getDeviceId(),

	                    AlertStatus.ACTIVE);

	    alerts.stream()

	            .filter(alert ->
	                    alertName.equals(alert.getName()))

	            .forEach(alert -> {

	                alert.setStatus(
	                        AlertStatus.RESOLVED);

	                alert.setResolvedAt(
	                        java.time.LocalDateTime.now());
	                alert.setResolvedBy(
	                        "SYSTEM");

	                alertRepository.save(
	                        alert);

	                AlertHistory history =
	                        AlertHistory.builder()
	                                .alertId(
	                                        alert.getId())
	                                .action(
	                                        "AUTO_RESOLVED")
	                                .description(
	                                        "Alert resolved automatically from telemetry.")
	                                .timestamp(
	                                        java.time.LocalDateTime.now())
	                                .build();

	                alertHistoryRepository.save(
	                        history);
	            });
	}
}