package com.ami.dto.requests;

import com.ami.enums.RoleType;
import com.ami.enums.StatusType;

import lombok.Data;

@Data
public class UserFilterRequestDto {

    private String keyword;

    private RoleType role;

    private StatusType status;

    private int page = 0;

    private int size = 10;
}