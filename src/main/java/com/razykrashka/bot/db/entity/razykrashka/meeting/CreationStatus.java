package com.razykrashka.bot.db.entity.razykrashka.meeting;

public enum CreationStatus {
    DONE("Done"),
    IN_PROGRESS("In Progress"),
    ARCHIVED("Archived");

    private final String status;

    CreationStatus(String status) {
        this.status = status;
    }

    public String get() {
        return this.status;
    }
}