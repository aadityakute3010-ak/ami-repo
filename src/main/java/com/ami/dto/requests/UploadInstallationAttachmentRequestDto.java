package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadInstallationAttachmentRequestDto {

    @NotBlank(message = "Attachment type is required")
    private String attachmentType;

}