package com.ami.dto.responses;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionTrendDTO {

    private LocalDate date;

    private Double openingReading;

    private Double closingReading;

    private Double totalReading;

    private Double consumption;
}