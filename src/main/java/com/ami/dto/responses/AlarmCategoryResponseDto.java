package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmCategoryResponseDto {

    private Long system;

    private Long water;

    private Long gas;

    private Long energy;

    private Long solar;

    private Long network;

    private Long battery;

    private Long communication;

    private Long tamper;

    private Long leakage;

    private Long billing;

    private Long other;


    private Long valve;

    private Long recharge;

    private Long consumption;

}
