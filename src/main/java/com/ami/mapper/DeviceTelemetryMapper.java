package com.ami.mapper;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.ami.dto.requests.CreateDeviceTelemetryRequestDto;
import com.ami.dto.responses.DeviceTelemetryResponseDto;
import com.ami.entity.ArchivedDeviceTelemetry;
import com.ami.entity.DeviceTelemetry;
import com.ami.entity.telemetry.EnergyTelemetry;
import com.ami.entity.telemetry.GasTelemetry;
import com.ami.entity.telemetry.SolarTelemetry;
import com.ami.entity.User;
import com.ami.entity.telemetry.WaterTelemetry;
import com.ami.enums.SourceType;
import com.ami.entity.Device;
import com.ami.dto.responses.MapLocationResponseDto;
@Component
public class DeviceTelemetryMapper {
	
	public WaterTelemetry mapToWaterEntity(
            CreateDeviceTelemetryRequestDto request) {

        return WaterTelemetry.builder()

                .deviceId(request.getDeviceId())

                .pumpStatus(request.getPumpStatus())

                .flowRate(request.getFlowRate())

                .pressure(request.getPressure())

                .temperature(request.getTemperature())

                .ph(request.getPh())

                .tds(request.getTds())

                .turbidity(request.getTurbidity())

                .conductivity(request.getConductivity())

                .dissolvedOxygen(request.getDissolvedOxygen())

                .chlorineLevel(request.getChlorineLevel())

                .consumption(request.getConsumption())

                .leakDetected(request.getLeakDetected())

                .deviceOnline(request.getDeviceOnline())

                .batteryLevel(request.getBatteryLevel())

                .signalStrength(null)

                .tamperDetected(null)

                .valveStatus(request.getValveStatus())

                .pipelineHealthScore(request.getPipelineHealthScore())

                .sensorHealthScore(request.getSensorHealthScore())

                .status(request.getStatus())

                .readingTime(LocalDateTime.now())

                .runtimeHours(request.getRuntimeHours())

                .lastStartedAt(request.getLastStartedAt())

                .lastStoppedAt(request.getLastStoppedAt())

                .estimatedWaterLoss(request.getEstimatedWaterLoss())

                .leakLocation(request.getLeakLocation())

                .totalFlow(request.getTotalFlow())

                .leakSeverity(request.getLeakSeverity())

                .alarmActive(request.getAlarmActive())

                .emergencyShutdown(request.getEmergencyShutdown())

                .build();
    }
	 
    public GasTelemetry mapToGasEntity(
            CreateDeviceTelemetryRequestDto request) {

        return GasTelemetry.builder()

                .deviceId(request.getDeviceId())

                .pressure(request.getPressure())

                .temperature(request.getTemperature())

                .flowRate(request.getFlowRate())

                .consumption(request.getConsumption())

                .leakDetected(request.getLeakDetected())

                .deviceOnline(
                        request.getDeviceOnline() != null
                                ? request.getDeviceOnline()
                                : false)

                .batteryLevel(request.getBatteryLevel())

                .status(
                        request.getStatus() != null
                                ? request.getStatus()
                                : "ACTIVE")

                .readingTime(LocalDateTime.now())

                .gasConcentration(request.getGasConcentration())

                .gasDensity(request.getGasDensity())

                .gasQuality(request.getGasQuality())

                .alarmActive(
                        request.getAlarmActive() != null
                                ? request.getAlarmActive()
                                : false)

                .emergencyShutdown(
                        request.getEmergencyShutdown() != null
                                ? request.getEmergencyShutdown()
                                : false)

                .build();
    }
    public EnergyTelemetry mapToEnergyEntity(
            CreateDeviceTelemetryRequestDto request) {

        return EnergyTelemetry.builder()

                .deviceId(request.getDeviceId())

                .voltage(null)

                .current(null)

                .temperature(request.getTemperature())

                .consumption(request.getConsumption())

                .deviceOnline(request.getDeviceOnline())

                .batteryLevel(request.getBatteryLevel())

                .signalStrength(null)

                .tamperDetected(null)

                .status(request.getStatus())

                .readingTime(LocalDateTime.now())

                .alarmActive(request.getAlarmActive())

                .emergencyShutdown(request.getEmergencyShutdown())

                .build();
    }
    public SolarTelemetry mapToSolarEntity(
            CreateDeviceTelemetryRequestDto request) {

        return SolarTelemetry.builder()

                .deviceId(request.getDeviceId())

                .voltage(null)

                .current(null)

                .temperature(request.getTemperature())

                .consumption(request.getConsumption())

                .deviceOnline(request.getDeviceOnline())

                .batteryLevel(request.getBatteryLevel())

                .signalStrength(null)

                .status(request.getStatus())

                .readingTime(LocalDateTime.now())

                .alarmActive(request.getAlarmActive())

                .emergencyShutdown(request.getEmergencyShutdown())

                .build();
    }
    public ArchivedDeviceTelemetry mapToArchivedTelemetry(
            DeviceTelemetry telemetry,
            User archivedBy,
            String archiveReason) {

        ArchivedDeviceTelemetry archivedTelemetry =
                new ArchivedDeviceTelemetry();

        BeanUtils.copyProperties(
                telemetry,
                archivedTelemetry);

        archivedTelemetry.setOriginalTelemetryId(
                telemetry.getId());

        archivedTelemetry.setArchivedAt(
                LocalDateTime.now());

        archivedTelemetry.setArchivedBy(
                archivedBy);

        archivedTelemetry.setArchiveReason(
                archiveReason);

        return archivedTelemetry;
    }
    public ArchivedDeviceTelemetry mapToArchivedTelemetry(
            WaterTelemetry telemetry,
            User archivedBy,
            String archiveReason) {

        ArchivedDeviceTelemetry archived =
                new ArchivedDeviceTelemetry();

        BeanUtils.copyProperties(
                telemetry,
                archived);

        archived.setOriginalTelemetryId(
                telemetry.getId());

        archived.setSourceType(
                SourceType.WATER);

        archived.setArchivedAt(
                LocalDateTime.now());

        archived.setArchivedBy(
                archivedBy);

        archived.setArchiveReason(
                archiveReason);

        return archived;
    }
    public ArchivedDeviceTelemetry mapToArchivedTelemetry(
            GasTelemetry telemetry,
            User archivedBy,
            String archiveReason) {

        ArchivedDeviceTelemetry archived =
                new ArchivedDeviceTelemetry();

        BeanUtils.copyProperties(
                telemetry,
                archived);

        archived.setOriginalTelemetryId(
                telemetry.getId());

        archived.setSourceType(
                SourceType.GAS);

        archived.setArchivedAt(
                LocalDateTime.now());

        archived.setArchivedBy(
                archivedBy);

        archived.setArchiveReason(
                archiveReason);

        return archived;
    }
    public ArchivedDeviceTelemetry mapToArchivedTelemetry(
            EnergyTelemetry telemetry,
            User archivedBy,
            String archiveReason) {

        ArchivedDeviceTelemetry archived =
                new ArchivedDeviceTelemetry();

        BeanUtils.copyProperties(
                telemetry,
                archived);

        archived.setOriginalTelemetryId(
                telemetry.getId());

        archived.setSourceType(
                SourceType.ENERGY);

        archived.setArchivedAt(
                LocalDateTime.now());

        archived.setArchivedBy(
                archivedBy);

        archived.setArchiveReason(
                archiveReason);

        return archived;
    }
    public ArchivedDeviceTelemetry mapToArchivedTelemetry(
            SolarTelemetry telemetry,
            User archivedBy,
            String archiveReason) {

        ArchivedDeviceTelemetry archived =
                new ArchivedDeviceTelemetry();

        BeanUtils.copyProperties(
                telemetry,
                archived);

        archived.setOriginalTelemetryId(
                telemetry.getId());

        archived.setSourceType(
                SourceType.SOLAR);

        archived.setArchivedAt(
                LocalDateTime.now());

        archived.setArchivedBy(
                archivedBy);

        archived.setArchiveReason(
                archiveReason);

        return archived;
    }
    public DeviceTelemetry mapToTelemetry(
            ArchivedDeviceTelemetry archivedTelemetry) {

        DeviceTelemetry telemetry =
                new DeviceTelemetry();

        BeanUtils.copyProperties(
                archivedTelemetry,
                telemetry,
                "id",
                "originalTelemetryId",
                "archivedAt",
                "archivedBy",
                "archiveReason");

        telemetry.setId(null);

        return telemetry;
    }
    public WaterTelemetry mapToWaterTelemetry(
            ArchivedDeviceTelemetry archivedTelemetry) {

        WaterTelemetry telemetry =
                new WaterTelemetry();

        BeanUtils.copyProperties(
                archivedTelemetry,
                telemetry);

        telemetry.setId(null);

        return telemetry;
    }
    public GasTelemetry mapToGasTelemetry(
            ArchivedDeviceTelemetry archivedTelemetry) {

        GasTelemetry telemetry =
                new GasTelemetry();

        BeanUtils.copyProperties(
                archivedTelemetry,
                telemetry);

        telemetry.setId(null);

        return telemetry;
    }
    public EnergyTelemetry mapToEnergyTelemetry(
            ArchivedDeviceTelemetry archivedTelemetry) {

        EnergyTelemetry telemetry =
                new EnergyTelemetry();

        BeanUtils.copyProperties(
                archivedTelemetry,
                telemetry);

        telemetry.setId(null);

        return telemetry;
    }
    public SolarTelemetry mapToSolarTelemetry(
            ArchivedDeviceTelemetry archivedTelemetry) {

        SolarTelemetry telemetry =
                new SolarTelemetry();

        BeanUtils.copyProperties(
                archivedTelemetry,
                telemetry);

        telemetry.setId(null);

        return telemetry;
    }
    
    public DeviceTelemetry mapToDeviceTelemetry(
            CreateDeviceTelemetryRequestDto request) {

        return DeviceTelemetry.builder()
                .deviceId(request.getDeviceId())
                .sourceType(request.getSourceType())
                .flowRate(request.getFlowRate())
                .pressure(request.getPressure())
                .temperature(request.getTemperature())
                .ph(request.getPh())
                .tds(request.getTds())
                .turbidity(request.getTurbidity())
                .conductivity(request.getConductivity())
                .dissolvedOxygen(request.getDissolvedOxygen())
                .chlorineLevel(request.getChlorineLevel())
                .consumption(request.getConsumption())
                .totalFlow(request.getTotalFlow())
                .gasConcentration(request.getGasConcentration())
                .gasDensity(request.getGasDensity())
                .gasQuality(request.getGasQuality())
                .leakSeverity(request.getLeakSeverity())
                .alarmActive(request.getAlarmActive() != null
                        ? request.getAlarmActive()
                        : false)
                .emergencyShutdown(request.getEmergencyShutdown() != null
                        ? request.getEmergencyShutdown()
                        : false)
                .leakDetected(request.getLeakDetected())
                .deviceOnline(request.getDeviceOnline() != null
                        ? request.getDeviceOnline()
                        : false)
                .runtimeHours(request.getRuntimeHours())
                .lastStartedAt(request.getLastStartedAt())
                .lastStoppedAt(request.getLastStoppedAt())
                .batteryLevel(request.getBatteryLevel())
                .valveStatus(request.getValveStatus())
                .pipelineHealthScore(request.getPipelineHealthScore())
                .sensorHealthScore(request.getSensorHealthScore())
                .status(request.getStatus() != null
                        ? request.getStatus()
                        : "ACTIVE")
                .readingTime(LocalDateTime.now())
                .pumpStatus(request.getPumpStatus())
                .estimatedWaterLoss(request.getEstimatedWaterLoss())
                .leakLocation(request.getLeakLocation())
                .build();
    }

    public DeviceTelemetryResponseDto mapWaterResponse(
            WaterTelemetry telemetry) {

        return DeviceTelemetryResponseDto.builder()

                .id(telemetry.getId())

                .deviceId(telemetry.getDeviceId())

                .sourceType(SourceType.WATER)

                .flowRate(telemetry.getFlowRate())

                .pressure(telemetry.getPressure())

                .temperature(telemetry.getTemperature())

                .consumption(telemetry.getConsumption())

                .leakDetected(telemetry.getLeakDetected())

                .deviceOnline(telemetry.getDeviceOnline())

                .status(telemetry.getStatus())

                .readingTime(telemetry.getReadingTime())

                .batteryLevel(telemetry.getBatteryLevel())

                .valveStatus(telemetry.getValveStatus())

                .pumpStatus(telemetry.getPumpStatus())

                .pipelineHealthScore(telemetry.getPipelineHealthScore())

                .sensorHealthScore(telemetry.getSensorHealthScore())

                .ph(telemetry.getPh())

                .tds(telemetry.getTds())

                .turbidity(telemetry.getTurbidity())

                .conductivity(telemetry.getConductivity())

                .dissolvedOxygen(telemetry.getDissolvedOxygen())

                .chlorineLevel(telemetry.getChlorineLevel())

                .runtimeHours(telemetry.getRuntimeHours())

                .lastStartedAt(telemetry.getLastStartedAt())

                .lastStoppedAt(telemetry.getLastStoppedAt())

                .estimatedWaterLoss(telemetry.getEstimatedWaterLoss())

                .leakLocation(telemetry.getLeakLocation())

                .totalFlow(telemetry.getTotalFlow())

                .leakSeverity(telemetry.getLeakSeverity())

                .alarmActive(telemetry.getAlarmActive())

                .emergencyShutdown(telemetry.getEmergencyShutdown())

                .build();
    }

    
    public  DeviceTelemetryResponseDto mapGasResponse(
            GasTelemetry telemetry) {

        return DeviceTelemetryResponseDto.builder()

                .id(telemetry.getId())

                .deviceId(telemetry.getDeviceId())

                .sourceType(SourceType.GAS)

                .flowRate(telemetry.getFlowRate())

                .pressure(telemetry.getPressure())

                .temperature(telemetry.getTemperature())

                .consumption(telemetry.getConsumption())

                .leakDetected(telemetry.getLeakDetected())

                .deviceOnline(telemetry.getDeviceOnline())

                .status(telemetry.getStatus())

                .readingTime(telemetry.getReadingTime())

                .batteryLevel(telemetry.getBatteryLevel())

                .gasConcentration(telemetry.getGasConcentration())

                .gasDensity(telemetry.getGasDensity())

                .gasQuality(telemetry.getGasQuality())

                .alarmActive(telemetry.getAlarmActive())

                .emergencyShutdown(telemetry.getEmergencyShutdown())

                .build();
    }
   
    public DeviceTelemetryResponseDto mapEnergyResponse(
            EnergyTelemetry telemetry) {

        return DeviceTelemetryResponseDto.builder()

                .id(telemetry.getId())

                .deviceId(telemetry.getDeviceId())

                .sourceType(SourceType.ENERGY)

                .temperature(telemetry.getTemperature())

                .consumption(telemetry.getConsumption())

                .deviceOnline(telemetry.getDeviceOnline())

                .status(telemetry.getStatus())

                .readingTime(telemetry.getReadingTime())

                .batteryLevel(telemetry.getBatteryLevel())

                .alarmActive(telemetry.getAlarmActive())

                .emergencyShutdown(telemetry.getEmergencyShutdown())

                .build();
    }
    
    public DeviceTelemetryResponseDto mapSolarResponse(
            SolarTelemetry telemetry) {

        return DeviceTelemetryResponseDto.builder()

                .id(telemetry.getId())

                .deviceId(telemetry.getDeviceId())

                .sourceType(SourceType.SOLAR)

                .temperature(telemetry.getTemperature())

                .consumption(telemetry.getConsumption())

                .deviceOnline(telemetry.getDeviceOnline())

                .status(telemetry.getStatus())

                .readingTime(telemetry.getReadingTime())

                .batteryLevel(telemetry.getBatteryLevel())

                .alarmActive(telemetry.getAlarmActive())

                .emergencyShutdown(telemetry.getEmergencyShutdown())

                .build();
    }
    public MapLocationResponseDto mapLocationResponse(
            Device device) {

        return MapLocationResponseDto.builder()

                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .sourceType(device.getSourceType())
                .zone(device.getZone())
                .location(device.getLocation())
                .latitude(device.getLatitude())
                .longitude(device.getLongitude())
                .online(device.getOnline())

                .build();
    }
    
   
}