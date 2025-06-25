package isima.georganise.app.controller;

import isima.georganise.app.exception.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HttpStatusController {

    private static final String MESSAGE_ERROR = "Error: ";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT, reason = "Something went very wrong")
    public void handleUnknownError(@NotNull Exception ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not found")
    public void handleNotFoundError(@NotNull RuntimeException ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request")
    public void handleBadRequestError(@NotNull RuntimeException ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }

    @ExceptionHandler(WrongPasswordException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Wrong password")
    public void handleWrongPasswordError(@NotNull RuntimeException ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }

    @ExceptionHandler(UnimplementedException.class)
    @ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED, reason = "Not implemented")
    public void handleNotImplementedError(@NotNull RuntimeException ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }

    @ExceptionHandler(NotLoggedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Not logged")
    public void handleNotLoggedError(@NotNull RuntimeException ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
    public void handleUnauthorizedError(@NotNull RuntimeException ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Not logged")
    public void handleMissingRequestCookieError(@NotNull Exception ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Conflict")
    public void handleConflictError(@NotNull RuntimeException ex) {
        System.err.println(MESSAGE_ERROR + ex.getMessage());
    }
}