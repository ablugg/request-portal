package com.github.ablugg.requestportal.common;

import com.github.ablugg.requestportal.request.InvalidStatusTransitionException;
import com.github.ablugg.requestportal.request.RequestNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Turns exceptions into consistent JSON error responses, in one place, for every controller.
 * ProblemDetail is the standard error format (RFC 9457): type, title, status, detail.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RequestNotFoundException.class)
    public ProblemDetail notFound(RequestNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** 409 Conflict: the request was valid JSON, but clashes with the request's current state. */
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ProblemDetail invalidTransition(InvalidStatusTransitionException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** 400 with one message per failed field, so a form can show each next to its input. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validationFailed(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "One or more fields are invalid");
        problem.setProperty("errors", errors);
        return problem;
    }

    /** 400 for a body that is not valid JSON, or uses an unknown category or status. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail unreadableBody(HttpMessageNotReadableException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "The request body is missing or malformed");
    }
}
