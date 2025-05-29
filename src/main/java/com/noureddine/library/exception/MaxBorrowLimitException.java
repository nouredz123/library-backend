package com.noureddine.library.exception;

public class MaxBorrowLimitException extends RuntimeException {
    public MaxBorrowLimitException(String message) {
        super(message);
    }
}
