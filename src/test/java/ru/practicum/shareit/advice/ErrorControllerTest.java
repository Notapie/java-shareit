package ru.practicum.shareit.advice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.advice.model.ErrorResponse;
import ru.practicum.shareit.exception.*;

class ErrorControllerTest {
    private final ErrorController errorController = new ErrorController();

    @Test
    void notFound() {
        ErrorResponse result = errorController.notFound(new NotFoundException("Error"));
        Assertions.assertEquals(result.getDescription(), "Error");
    }

    @Test
    void forbidden() {
        ErrorResponse result = errorController.accessDenied(new ForbiddenException("Error"));
        Assertions.assertEquals(result.getDescription(), "Error");
    }

    @Test
    void validation() {
        ErrorResponse result = errorController.validationError(new ValidationException("Error"));
        Assertions.assertEquals(result.getDescription(), "Error");
    }

    @Test
    void save() {
        ErrorResponse result = errorController.saveError(new SaveException("Error", new Exception()));
        Assertions.assertEquals(result.getDescription(), "Error");
    }

    @Test
    void unknownState() {
        ErrorResponse result = errorController.unknownError(new UnknownStateException("Error"));
        Assertions.assertEquals(result.getDescription(), "Error");
    }

    @Test
    void unavailable() {
        ErrorResponse result = errorController.unavailableError(new UnavailableException("Error"));
        Assertions.assertEquals(result.getDescription(), "Error");
    }
}
