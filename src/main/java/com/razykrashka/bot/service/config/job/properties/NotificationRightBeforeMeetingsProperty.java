package com.razykrashka.bot.service.config.job.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRightBeforeMeetingsProperty implements JobRunnable {
    boolean enabled;
    String cronExp;
    String name;
    Runnable job;
}