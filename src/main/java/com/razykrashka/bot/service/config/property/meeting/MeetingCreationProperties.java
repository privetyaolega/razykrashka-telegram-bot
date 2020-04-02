package com.razykrashka.bot.service.config.property.meeting;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeetingCreationProperties {
    Integer upperHourLimitToday;
    Integer hourAdvance;
}