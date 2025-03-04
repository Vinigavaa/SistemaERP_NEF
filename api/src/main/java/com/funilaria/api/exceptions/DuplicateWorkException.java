// src/main/java/com/funilaria/api/exceptions/DuplicateWorkException.java
package com.funilaria.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateWorkException extends RuntimeException {
    public DuplicateWorkException(String message) {
        super(message);
    }
}