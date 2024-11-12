package ru.kolobkevic.tasktracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleObjectAlreadyExists() {
        return new ResponseEntity<>(new ErrorResponse("Object already exists"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserBadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleUserBadCredentials() {
        return new ResponseEntity<>(new ErrorResponse("Wrong credentials"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound() {
        return new ResponseEntity<>(new ErrorResponse("Unable to find a task"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidArgumentsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArguments(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
