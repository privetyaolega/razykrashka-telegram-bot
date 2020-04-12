package com.razykrashka.bot.service.config.job;


import com.razykrashka.bot.service.config.job.properties.JobProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Getter
@Component
public class ThreadPoolTaskSchedulerWrapper {
    @Autowired
    JobProperties jobProperties;
    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;
    Map<String, ScheduledFuture<?>> executingTask = new HashMap<>();
}