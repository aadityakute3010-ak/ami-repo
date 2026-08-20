package com.ami.dto.responses;

import java.time.LocalDate;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyReadingResponseDTO {

    private Long deviceId;

    private String deviceCode;

    private String deviceName;

    private LocalDate date;

    private Double openingReading;

    private Double closingReading;

    private Double totalReading;

    private Double dailyConsumption;

    private List<HourlyReadingDTO> readings;
}