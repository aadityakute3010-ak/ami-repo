package com.ami.dto.requests;

import lombok.Data;

@Data
public class FieldVisitRequestDto {

    private String latitude;

    private String longitude;

    private String visitNotes;
}