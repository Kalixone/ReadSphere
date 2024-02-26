package mate.academy.springbootintro.exception;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<String> errors;

    public CustomErrorResponse(LocalDateTime timestamp, HttpStatus status,
                               String error, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status.value();
        this.error = error;
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<String> getErrors() {
        return errors;
    }
}
