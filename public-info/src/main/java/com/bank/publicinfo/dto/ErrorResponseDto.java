package com.bank.publicinfo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

public class ErrorResponseDto {

    private String errorCode;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime timestamp;

    public ErrorResponseDto(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
