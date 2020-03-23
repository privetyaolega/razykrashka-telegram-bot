package com.razykrashka.bot.exception;

public class EntityWasNotFoundException extends RuntimeException {
    public EntityWasNotFoundException(String message) {
        super(message);
    }
}