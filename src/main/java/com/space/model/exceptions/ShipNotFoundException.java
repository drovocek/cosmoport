package com.space.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no such ship")
public class ShipNotFoundException extends RuntimeException {
    public ShipNotFoundException() {
    }
}
