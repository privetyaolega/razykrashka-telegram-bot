package com.razykrashka.bot.stage.test;

import com.razykrashka.bot.service.config.job.ThreadPoolTaskSchedulerWrapper;
import com.razykrashka.bot.service.config.job.task.AvailableMeetingsNotificationJob;
import com.razykrashka.bot.service.config.job.properties.JobProperties;
import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class TestJobStage extends MainStage {

    @Autowired
    ThreadPoolTaskSchedulerWrapper threadPoolTaskSchedulerWrapper;

    @Override
    public void handleRequest() {
        JobProperties jobProperties = threadPoolTaskSchedulerWrapper.getJobProperties();
        AvailableMeetingsNotificationJob availableJob = jobProperties.getMeeting().getNotification().getAvailable();
        threadPoolTaskSchedulerWrapper.getExecutingTask()
                .get(availableJob.getName())
                .cancel(false);
        String newCron = updateHelper.getMessageText().replace("/job", "");
        threadPoolTaskSchedulerWrapper.getThreadPoolTaskScheduler()
                .schedule(availableJob, new CronTrigger(newCron));
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageContains("/job");
    }
}