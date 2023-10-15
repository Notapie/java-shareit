package ru.practicum.shareit.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.advice.model.ErrorResponse;
import ru.practicum.shareit.exception.*;

@RestControllerAdvice
@Slf4j
public class ErrorController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse accessDenied(final ForbiddenException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "Access denied", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final NotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "Not found", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse alreadyExists(final AlreadyExistsException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "Already exists", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse saveError(final SaveException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "Saving error", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse deleteError(final DeleteException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "Delete error", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationError(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "Validation error", e.getMessage()
        );
    }
}
