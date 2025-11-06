package hbm.authservice.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String, String>> handleExpiredJwtException(ExpiredJwtException ex) {
        // Trả về 401 Unauthorized với body JSON tùy chỉnh
        Map<String, String> errorBody = Map.of(
                "error", "Unauthorized",
                "message", "Token expired"
        );
        return new ResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }

    // ✅ Bắt ResponseStatusException (mà bạn ném cho các lỗi token khác)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        // Sử dụng HttpStatus và message từ ResponseStatusException
        Map<String, String> errorBody = Map.of(
                "error", ex.getCause().getMessage(),
                "message", ex.getReason()
        );
        return new ResponseEntity<>(errorBody, ex.getStatusCode());
    }
}
