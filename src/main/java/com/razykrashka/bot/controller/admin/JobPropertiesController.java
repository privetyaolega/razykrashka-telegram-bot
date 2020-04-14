package com.razykrashka.bot.controller.admin;

import com.razykrashka.bot.service.config.job.ThreadPoolTaskSchedulerWrapper;
import com.razykrashka.bot.service.config.job.properties.*;
import com.razykrashka.bot.service.config.property.meeting.MeetingProperties;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ScheduledFuture;

@Log4j2
@Controller
@RequestMapping("/admin")
public class JobPropertiesController {

    @Autowired
    ThreadPoolTaskSchedulerWrapper threadPoolTaskSchedulerWrapper;
    @Autowired
    UpdateHelper updateHelper;
    @Autowired
    MeetingProperties meetingProperties;
    @Autowired
    JobProperties jobProperties;

    @PostMapping("/save-job-props")
    public String saveJobProperties(@ModelAttribute("prop") JobMeetingProperties job) {
        AvailableMeetingsProperty availableJobNewData = job.getNotification().getAvailable();
        AvailableMeetingsProperty availableJobFromProperty = jobProperties.getMeeting().getNotification().getAvailable();
        availableJobFromProperty.setCronExp(availableJobNewData.getCronExp());
        availableJobFromProperty.setEnabled(availableJobNewData.isEnabled());
        changeJobProperties(availableJobFromProperty);

        UpcomingMeetingsProperty upcomingJobNewData = job.getNotification().getUpcoming();
        UpcomingMeetingsProperty upcomingJobFromProperty = jobProperties.getMeeting().getNotification().getUpcoming();
        upcomingJobFromProperty.setCronExp(upcomingJobNewData.getCronExp());
        upcomingJobFromProperty.setEnabled(upcomingJobNewData.isEnabled());
        changeJobProperties(upcomingJobFromProperty);
        return "redirect:/admin/job-properties";
    }

    private void changeJobProperties(JobRunnable job) {
        log.info("Changing properties for {}", job.getName());
        if (threadPoolTaskSchedulerWrapper.getExecutingTask().containsKey(job.getName())) {
            threadPoolTaskSchedulerWrapper.getExecutingTask().get(job.getName()).cancel(false);
        }

        if (job.isEnabled()) {
            log.info("Job '{}' has been ENABLED with cron exp: {}", job.getName(), job.getCronExp());
            ThreadPoolTaskScheduler threadPoolTaskScheduler = threadPoolTaskSchedulerWrapper.getThreadPoolTaskScheduler();
            ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(job.getJob(), job.getCronTrigger());
            threadPoolTaskSchedulerWrapper.getExecutingTask().put(job.getName(), schedule);
        } else {
            log.info("Job '{}' has been DISABLED", job.getName());
        }
    }

    @GetMapping("/job-properties")
    public String jobProperties(Model model) {
        JobMeetingProperties props = jobProperties.getMeeting();
        model.addAttribute("properties", props);
        return "properties/job-properties.html";
    }
}