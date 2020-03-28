package com.razykrashka.bot.db.entity.razykrashka.meeting;


import lombok.Getter;

@Getter
public enum MeetingFormatEnum {
    NA("na"),
    ONLINE("Online"),
    OFFLINE("Offline");

    private final String format;

    MeetingFormatEnum(String format) {
        this.format = format;
    }
}