package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourlyReadingDTO {

    private String label;          // R0, R1, R2...
    private Integer hour;          // 0 to 23
    private LocalDateTime timestamp;

    private Double reading;
    private Double consumption;
}