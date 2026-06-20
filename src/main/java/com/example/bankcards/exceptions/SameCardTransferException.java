package com.example.bankcards.exceptions;

public class SameCardTransferException extends RuntimeException {
    public SameCardTransferException(String message) {
        super(message);
    }
}
