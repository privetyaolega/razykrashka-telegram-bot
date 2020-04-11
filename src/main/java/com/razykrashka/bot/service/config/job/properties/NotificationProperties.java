package com.razykrashka.bot.service.config.job.properties;

import com.razykrashka.bot.service.config.job.task.AvailableMeetingsNotificationJob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationProperties {
    UpcomingProperties upcoming;
    AvailableMeetingsNotificationJob available;
}