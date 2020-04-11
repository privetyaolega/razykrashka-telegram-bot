package com.razykrashka.bot.service.config.property.meeting;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeetingCreationProperties {
    Integer upperHourLimitToday;
    Integer daysAdvanceMaximum;
    Integer hourAdvance;
    Boolean notificationGroup;
}