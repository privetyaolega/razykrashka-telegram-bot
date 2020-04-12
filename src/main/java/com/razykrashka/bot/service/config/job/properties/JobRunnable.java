package com.razykrashka.bot.service.config.job.properties;

import org.springframework.scheduling.support.CronTrigger;

public interface JobRunnable {
    boolean isEnabled();

    String getCronExp();

    String getName();

    Runnable getJob();

    default CronTrigger getCronTrigger() {
        return new CronTrigger(getCronExp());
    }
}