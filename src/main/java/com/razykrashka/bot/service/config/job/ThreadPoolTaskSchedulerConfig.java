package com.razykrashka.bot.service.config.job;

import com.razykrashka.bot.service.config.job.properties.JobProperties;
import com.razykrashka.bot.service.config.job.properties.JobRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledFuture;

@Configuration
public class ThreadPoolTaskSchedulerConfig {

    @Autowired
    JobProperties jobProperties;
    ThreadPoolTaskSchedulerWrapper threadPoolTaskSchedulerWrapper;

    @Bean
    public ThreadPoolTaskSchedulerWrapper threadPoolTaskScheduler() {
        threadPoolTaskSchedulerWrapper = new ThreadPoolTaskSchedulerWrapper();

        ThreadPoolTaskScheduler threadPoolTaskScheduler = threadPoolTaskSchedulerWrapper.getThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("Job Task Scheduler");
        threadPoolTaskScheduler.initialize();

        registerJob(jobProperties.getMeeting().getNotification().getAvailable());

        return threadPoolTaskSchedulerWrapper;
    }

    private void registerJob(JobRunnable jobRunnable) {
        if (jobRunnable.isEnabled()) {
            ThreadPoolTaskScheduler threadPoolTaskScheduler = threadPoolTaskSchedulerWrapper.getThreadPoolTaskScheduler();
            ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(jobRunnable, jobRunnable.getCronTrigger());
            threadPoolTaskSchedulerWrapper.getExecutingTask().put(jobRunnable.getName(), schedule);
        }
    }
}