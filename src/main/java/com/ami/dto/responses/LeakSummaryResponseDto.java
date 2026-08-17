package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeakSummaryResponseDto {

    private String deviceId;

    private String deviceName;

    private Boolean leakDetected;

    private String severity;

    private String location;

    private Double pressure;

    private Double flowRate;

    private LocalDateTime detectedAt;

    private String status;
    
    private Double estimatedLoss;

    private String recommendation;

    private Boolean criticalLeak;

    private Integer leakScore;
}