package arundaon.ytclone.controllers;

import arundaon.ytclone.models.WebResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<String>> constraintViolationException(ConstraintViolationException cve) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(WebResponse.<String>builder().errors(cve.getMessage()).build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> responseStatusException(ResponseStatusException rse){
        return ResponseEntity.status(rse.getStatusCode()).body(WebResponse.<String>builder().errors(rse.getReason()).build());
    }
}
