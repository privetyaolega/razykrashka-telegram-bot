package com.razykrashka.bot.service.config.job.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@ToString
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableMeetingsProperty implements JobRunnable {
    boolean enabled;
    String cronExp;
    String name;
    Runnable job;
}