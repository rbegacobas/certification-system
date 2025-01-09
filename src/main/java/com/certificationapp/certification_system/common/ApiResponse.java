package com.certificationapp.certification_system.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for consistent response structure across the application.
 * This class implements the Response Wrapper pattern for standardized API responses.
 *
 * @param <T> The type of data being returned
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    /**
     * HTTP status code of the response
     */
    private HttpStatus status;

    /**
     * Response timestamp in ISO format
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    /**
     * Response message, usually used for error descriptions or success confirmations
     */
    private String message;

    /**
     * Actual response data
     */
    private T data;

    /**
     * Error details, only included in error responses
     */
    private String errorDetails;

    /**
     * Creates a success response with data
     *
     * @param data    The response data
     * @param message Success message
     * @param <T>     Type of response data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(data)
                .message(message)
                .build();
    }

    /**
     * Creates an error response
     *
     * @param status      HTTP status code
     * @param message     Error message
     * @param errorDetails Detailed error description
     * @param <T>         Type of response data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorDetails) {
        return ApiResponse.<T>builder()
                .status(status)
                .timestamp(LocalDateTime.now())
                .message(message)
                .errorDetails(errorDetails)
                .build();
    }
}
