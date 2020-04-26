package com.razykrashka.bot.db.entity.razykrashka.meeting;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SpeakingLevel {
    ELEMENTARY("Elementary", "A1", 1),
    PRE_INTERMEDIATE("Pre-Intermediate", "A2", 2),
    INTERMEDIATE("Intermediate", "B1", 3),
    UPPER_INTERMEDIATE("Upper-Intermediate", "B2", 4),
    ADVANCED("Advanced", "C1", 5),
    NATIVE("Native", "C2", 6);

    String label;
    String levelLabel;
    int levelPoint;
}