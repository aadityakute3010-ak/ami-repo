package com.ami.dto.requests;

import lombok.Data;

@Data
public class AssignAlertRequestDto {

    /*
     * ADMIN or DEVICE
     */
    private String assignmentType;

    /*
     * Required when assignmentType = ADMIN
     */
    private Long adminId;

    /*
     * Required when assignmentType = DEVICE
     */
    private String deviceId;

    /*
     * Optional reason/comment.
     */
    private String reason;
}