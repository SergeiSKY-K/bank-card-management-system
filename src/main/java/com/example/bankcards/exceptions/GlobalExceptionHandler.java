package com.example.bankcards.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CardNotFoundException.class)
    public Map<String, String> handleCardNotFound(CardNotFoundException ex) {
        return Map.of(
                "error", "CARD_NOT_FOUND",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> handleUserNotFound(UserNotFoundException ex) {
        return Map.of(
                "error", "USER_NOT_FOUND",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, String> handleAccessDenied(AccessDeniedException ex) {
        return Map.of(
                "error", "ACCESS_DENIED",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientFundsException.class)
    public Map<String, String> handleInsufficientFunds(InsufficientFundsException ex) {
        return Map.of(
                "error", "INSUFFICIENT_FUNDS",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CardBlockedException.class)
    public Map<String, String> handleCardBlocked(CardBlockedException ex) {
        return Map.of(
                "error", "CARD_BLOCKED",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CardExpiredException.class)
    public Map<String, String> handleCardExpired(CardExpiredException ex) {
        return Map.of(
                "error", "CARD_EXPIRED",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SameCardTransferException.class)
    public Map<String, String> handleSameCardTransfer(SameCardTransferException ex) {
        return Map.of(
                "error", "SAME_CARD_TRANSFER",
                "message", ex.getMessage()
        );
    }
}