package com.ami.dto.requests;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldVisitRequestDto {

    @NotNull
    private LocalDateTime visitDate;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private Double latitude;

    private Double longitude;

    private String observation;

    private String photoUrl;

    private String status;
}