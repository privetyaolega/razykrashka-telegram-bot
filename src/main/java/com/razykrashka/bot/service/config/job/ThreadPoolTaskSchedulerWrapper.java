package com.razykrashka.bot.service.config.job;


import com.razykrashka.bot.service.config.job.properties.JobProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Getter
public class ThreadPoolTaskSchedulerWrapper {
    @Autowired
    JobProperties jobProperties;
    Map<String, ScheduledFuture<?>> executingTask = new HashMap<>();
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
}