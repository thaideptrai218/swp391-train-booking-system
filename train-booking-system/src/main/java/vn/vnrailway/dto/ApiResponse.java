package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude; // Optional: for Jackson to exclude null fields

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Optional: Jackson will not include null fields in JSON
public class ApiResponse<T> {
    private String status; // e.g., "success", "error"
    private String message; // Optional: user-friendly message or error description
    private T data; // Optional: payload for success responses

    // Static factory methods for convenience
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("success", message, null);
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", null, data);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>("error", message, null);
    }

    // Constructor for error without data
    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
}
