package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldVisitResponseDto {

    private Long id;

    private Long engineerId;

    private String engineerName;

    private LocalDateTime visitDate;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private Double latitude;

    private Double longitude;

    private String observation;

    private String photoUrl;

    private String status;
}