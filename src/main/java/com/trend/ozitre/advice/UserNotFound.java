package com.trend.ozitre.advice;

import java.io.Serial;

public class UserNotFound extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 3022345606060435213L;

    public UserNotFound(String message) {
        super(message);
    }
}
