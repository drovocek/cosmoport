package com.space.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Illegal id value")
public class IllegalArgException extends RuntimeException {
    public IllegalArgException() {
    }
}
