package com.ami.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignAdminRequestDto {

    @NotNull
    private Long adminId;
}