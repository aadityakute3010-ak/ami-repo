// mapper/ExportFileResponseMapper.java
package com.ami.mapper;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ami.dto.responses.ExportFileResponseDto;

@Component
public class ExportFileResponseMapper {

	public ResponseEntity<byte[]> toAttachmentResponse(ExportFileResponseDto exportedFile) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(exportedFile.getContentType()));
		headers.setContentDisposition(ContentDisposition.attachment().filename(exportedFile.getFileName()).build());

		return ResponseEntity.ok().headers(headers).body(exportedFile.getFile());
	}
}