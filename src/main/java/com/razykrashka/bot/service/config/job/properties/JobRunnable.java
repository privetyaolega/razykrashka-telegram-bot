package com.razykrashka.bot.service.config.job.properties;

import org.springframework.scheduling.support.CronTrigger;

public interface JobRunnable extends Runnable {
    boolean isEnabled();

    String getCronExp();

    String getName();

    default CronTrigger getCronTrigger() {
        return new CronTrigger(getCronExp());
    }
}