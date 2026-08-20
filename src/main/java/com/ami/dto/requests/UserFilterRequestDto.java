package com.ami.dto.requests;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.ami.enums.RoleType;
import com.ami.enums.StatusType;

import lombok.Data;

@Data
public class UserFilterRequestDto {

    private String keyword;

    private RoleType role;

    private StatusType status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    private int page = 0;

    private int size = 10;
}