package com.razykrashka.bot.service.config.job;

import com.razykrashka.bot.service.config.job.properties.JobProperties;
import com.razykrashka.bot.service.config.job.properties.JobRunnable;
import com.razykrashka.bot.service.config.job.task.AvailableMeetingsNotificationJob;
import com.razykrashka.bot.service.config.job.task.NotificationRightBeforeMeetingJob;
import com.razykrashka.bot.service.config.job.task.UpcomingMeetingsNotificationJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledFuture;

@Configuration
public class ThreadPoolTaskSchedulerConfig {

    final ThreadPoolTaskSchedulerWrapper threadPoolTaskSchedulerWrapper;
    final UpcomingMeetingsNotificationJob upcomingMeetingsNotificationJob;
    final AvailableMeetingsNotificationJob availableMeetingsNotificationJob;
    final NotificationRightBeforeMeetingJob notificationRightBeforeMeetingJob;

    public ThreadPoolTaskSchedulerConfig(ThreadPoolTaskSchedulerWrapper threadPoolTaskSchedulerWrapper,
                                         UpcomingMeetingsNotificationJob upcomingMeetingsNotificationJob,
                                         AvailableMeetingsNotificationJob availableMeetingsNotificationJob,
                                         NotificationRightBeforeMeetingJob notificationRightBeforeMeetingJob) {
        this.threadPoolTaskSchedulerWrapper = threadPoolTaskSchedulerWrapper;
        this.upcomingMeetingsNotificationJob = upcomingMeetingsNotificationJob;
        this.availableMeetingsNotificationJob = availableMeetingsNotificationJob;
        this.notificationRightBeforeMeetingJob = notificationRightBeforeMeetingJob;
    }

    @Bean
    public ThreadPoolTaskSchedulerWrapper threadPoolTaskSchedulerWrapper() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = threadPoolTaskSchedulerWrapper.getThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("Job Task Scheduler");
        threadPoolTaskScheduler.initialize();

        JobProperties jobProperties = threadPoolTaskSchedulerWrapper.getJobProperties();
        jobProperties.getMeeting().getNotification().getUpcoming().setJob(upcomingMeetingsNotificationJob);
        jobProperties.getMeeting().getNotification().getAvailable().setJob(availableMeetingsNotificationJob);
        jobProperties.getMeeting().getNotification().getRightBefore().setJob(notificationRightBeforeMeetingJob);
        registerJob(jobProperties.getMeeting().getNotification().getAvailable());
        registerJob(jobProperties.getMeeting().getNotification().getUpcoming());
        registerJob(jobProperties.getMeeting().getNotification().getRightBefore());

        return threadPoolTaskSchedulerWrapper;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    private void registerJob(JobRunnable jobRunnable) {
        if (jobRunnable.isEnabled()) {
            ThreadPoolTaskScheduler threadPoolTaskScheduler = threadPoolTaskSchedulerWrapper.getThreadPoolTaskScheduler();
            ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(jobRunnable.getJob(), jobRunnable.getCronTrigger());
            threadPoolTaskSchedulerWrapper.getExecutingTask().put(jobRunnable.getName(), schedule);
        }
    }
}