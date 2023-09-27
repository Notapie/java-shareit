package ru.practicum.shareit.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.advice.model.ErrorResponse;
import ru.practicum.shareit.exception.AuthorizationException;

@RestControllerAdvice
@Slf4j
public class ErrorController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse authorizationException(AuthorizationException e) {
        log.debug(e.getMessage());
        return new ErrorResponse(
                "No authorization", e.getMessage()
        );
    }
}
