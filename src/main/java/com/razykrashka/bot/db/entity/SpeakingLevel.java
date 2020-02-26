package com.razykrashka.bot.db.entity;


import lombok.Getter;

@Getter
public enum SpeakingLevel {
    ELEMENTARY("Elementary"),
    PRE_INTERMEDIATE("Pre-Intermediate"),
    INTERMEDIATE("Intermediate"),
    UPPER_INTERMEDIATE("Upper-Intermediate"),
    ADVANCED("Advanced"),
    NATIVE("Native");

    private final String level;

    SpeakingLevel(String level) {
        this.level = level;
    }
}