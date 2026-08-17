package com.ami.dto.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRuleAssignmentPageResponseDto {

    private List<AlertRuleAssignmentResponseDto> content;

    private long totalElements;

    private int totalPages;

    private int number;

    private int size;

    private boolean first;

    private boolean last;

    private boolean empty;
}