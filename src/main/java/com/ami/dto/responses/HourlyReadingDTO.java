package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourlyReadingDTO {

    private LocalDateTime timestamp;

    private Double reading;

    private Double consumption;
}