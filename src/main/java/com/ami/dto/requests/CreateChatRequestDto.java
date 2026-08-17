package com.ami.dto.requests;

import lombok.Data;

@Data
public class CreateChatRequestDto {

    private String userId;

    private String message;

    private String module;
}