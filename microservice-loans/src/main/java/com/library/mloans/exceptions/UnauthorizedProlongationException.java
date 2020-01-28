package com.library.mloans.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedProlongationException extends RuntimeException {
    public UnauthorizedProlongationException(String message) {
        super(message);
    }
}
