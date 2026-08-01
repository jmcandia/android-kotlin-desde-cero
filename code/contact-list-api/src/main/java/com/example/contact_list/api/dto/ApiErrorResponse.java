package com.example.contact_list.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    @Schema(description = "Date and time of the error", example = "2023-01-01T00:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Status code", example = "404")
    private Integer status;

    @Schema(description = "Status description", example = "Not Found")
    private String error;

    @Schema(description = "Exception message", example = "Resource not found")
    private String message;

    @Schema(description = "Request path", example = "/api/contact/1")
    private String path;

    @Schema(description = "List of fields with validation errors")
    private List<String> errors;
}
