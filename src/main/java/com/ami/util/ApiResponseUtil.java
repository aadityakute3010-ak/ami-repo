package com.ami.util;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import com.ami.dto.responses.ApiResponse;

public class ApiResponseUtil {

    private ApiResponseUtil() {
    }

    public static <T> ApiResponse<T> success(

            String message,

            T data) {

        return ApiResponse.<T>builder()

                .success(true)

                .message(message)

                .timestamp(LocalDateTime.now())

                .data(data)

                .build();
    }

    public static <T> ApiResponse<Page<T>> success(

            String message,

            Page<T> page) {

        return ApiResponse.<Page<T>>builder()

                .success(true)

                .message(message)

                .timestamp(LocalDateTime.now())

                .data(page)

                .page(page.getNumber())

                .size(page.getSize())

                .totalElements(page.getTotalElements())

                .totalPages(page.getTotalPages())

                .build();
    }

    public static <T> ApiResponse<T> failure(

            String message) {

        return ApiResponse.<T>builder()

                .success(false)

                .message(message)

                .timestamp(LocalDateTime.now())

                .build();
    }
}