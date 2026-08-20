package com.ami.mapper;

import org.springframework.stereotype.Component;

import com.ami.dto.responses.PayloadDetailDTO;
import com.ami.dto.responses.PayloadSummaryDTO;
import com.ami.entity.Payload;

@Component
public class PayloadMapper {

    /**
     * Used for Payload Monitoring Table 
     */
    public PayloadSummaryDTO toSummary(Payload payload) {

        return PayloadSummaryDTO.builder()
                .id(payload.getId())

                // Device Information
                .deviceName(payload.getDevice().getDeviceName())
                .meterNumber(payload.getDevice().getSerialNumber())
                .macAddress(payload.getDevice().getMacAddress())

                // Payload Information
                .timestamp(payload.getReceivedAt())
                .startReading(payload.getStartReading())
                .endReading(payload.getEndReading())
                .batteryPercentage(payload.getBatteryPercentage())
                .signalQuality(payload.getSignalQuality())
                .status(payload.getStatus())

                .build();
    }

    /**
     * Used for Payload Detail Modal
     */
    public PayloadDetailDTO toDetail(Payload payload) {

        return PayloadDetailDTO.builder()

                .id(payload.getId())

                // =====================================================
                // Device Information
                // =====================================================

                .deviceName(payload.getDevice().getDeviceName())
                .meterNumber(payload.getDevice().getSerialNumber())
                .consumerNumber(payload.getConsumerNumber())
                .macAddress(payload.getDevice().getMacAddress())
                .firmwareVersion(payload.getFirmwareVersion())
                .simNumber(payload.getSimNumber())

                // =====================================================
                // Meter Data
                // =====================================================

                .startReading(payload.getStartReading())
                .endReading(payload.getEndReading())
                .consumption(payload.getConsumption())
                .startBalance(payload.getStartBalance())
                .endBalance(payload.getEndBalance())

                // =====================================================
                // Communication Data
                // =====================================================

                .batteryPercentage(payload.getBatteryPercentage())
                .signalQuality(payload.getSignalQuality())
                .signalPower(payload.getSignalPower())
                .snr(payload.getSnr())

                // =====================================================
                // Timeline
                // =====================================================

                .receivedAt(payload.getReceivedAt())
                .valveStatus(payload.getValveStatus())
                .sensorStatus(payload.getSensorStatus())
                .status(payload.getStatus())

                // =====================================================
                // Raw Payload
                // =====================================================

                .rawPayload(payload.getRawPayload())

                .build();
    }
}