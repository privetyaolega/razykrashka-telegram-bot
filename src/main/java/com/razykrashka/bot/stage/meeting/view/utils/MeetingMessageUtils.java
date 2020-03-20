package com.razykrashka.bot.stage.meeting.view.utils;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource(value = "classpath:/props/razykrashka.yaml", factory = YamlPropertyLoaderFactory.class)
public class MeetingMessageUtils {

    @Value("${razykrashka.bot.username}")
    String botUserName;

    final static String GOOGLE_MAP_LINK_PATTERN = "https://www.google.com/maps/search/?api=1&query=%s,%s";
    final static String DATE_TIME_PATTERN = "dd MMMM (EEEE) ⏰ HH:mm";
    final static String DATE_PATTERN = "dd MMMM (EEEE)";
    final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.ENGLISH);

    public String createMeetingsText(List<Meeting> userMeetings, int meetingAmount) {
        return userMeetings.stream()
                .map(this::createSingleMeetingMainInformationText)
                .collect(Collectors.joining("\n\n", "\uD83D\uDCAB Найдено " + meetingAmount + " встреч(и)\n\n", ""));
    }

    public String createSingleMeetingFullText(Meeting meeting) {
        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        return "<code>MEETING # " + meeting.getId() + "</code>\n" +
                meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER) + "\n"
                + "\uD83D\uDCCD" + getLocationLink(meeting) + "\n"
                + meetingInfo.getSpeakingLevel().toString() + "\n"
                + meetingInfo.getTopic() + "\n"
                + meetingInfo.getQuestions().replace("●", "\n●") + "\n";
    }

    public String createSingleMeetingMainInformationText(Meeting meeting) {
        return meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER) + "\n"
                + "\uD83D\uDCCD" + getLocationLink(meeting) + "\n"
                + meeting.getMeetingInfo().getSpeakingLevel().toString() + "\n"
                + meeting.getMeetingInfo().getTopic() + "\n"
                + "INFORMATION: /meeting" + meeting.getId();
    }

    public String createMeetingInfoDuringCreation(Meeting meeting) {
        StringBuilder sb = new StringBuilder();

        if (meeting.getMeetingDateTime() != null) {
            String pattern = DATE_PATTERN;
            if (meeting.getMeetingDateTime().getHour() != 0) {
                pattern = DATE_TIME_PATTERN;
            }
            sb.append("DATE: ").append(meeting.getMeetingDateTime()
                    .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)));
        }

        if (meeting.getLocation() != null) {
            String locationLink = getLocationLink(meeting);
            sb.append("\n\nADDRESS: \uD83D\uDCCD").append(locationLink);
        }

        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        if (meetingInfo != null) {
            if (meetingInfo.getSpeakingLevel() != null) {
                sb.append("\n\nLEVEL: ").append(meetingInfo.getSpeakingLevel().getLevel());
            }
            if (meetingInfo.getParticipantLimit() != null) {
                sb.append("\n\nPARTICIPANT LIMIT: ").append(meetingInfo.getParticipantLimit());
            }
            if (meetingInfo.getTopic() != null) {
                sb.append("\n\nTOPIC: ").append(meetingInfo.getTopic());
            }
            if (meetingInfo.getQuestions() != null) {
                sb.append("\n\nQUESTION: \n").append(meetingInfo.getQuestions());
            }
        }
        return sb.append("\n\n\n").toString();
    }

    public String createMeetingInfoGroup(Meeting meeting) {
        return Emoji.FIRE + " NEW MEETING! " + Emoji.FIRE + "\n" + "\n" +
                Emoji.BIG_SUN + "\n" +
                Emoji.SMALL_SUN + "   " + Emoji.CLOCK + "   " + meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER) + "\n" +
                Emoji.BIG_SUN + "\n" +
                Emoji.SMALL_SUN + "   " + Emoji.LOCATION + "   " + getLocationLink(meeting) + "\n" +
                Emoji.BIG_SUN + "\n" +
                Emoji.SMALL_SUN + "   " + Emoji.PEOPLE + "   " + meeting.getMeetingInfo().getParticipantLimit() + "\n" +
                Emoji.BIG_SUN + "\n" +
                Emoji.SMALL_SUN + "   " + Emoji.SPEECH_CLOUD + "   " + meeting.getMeetingInfo().getTopic() +
                " (" + meeting.getMeetingInfo().getSpeakingLevel().getLevel() + ")" + "\n" +
                Emoji.BIG_SUN +
                "\n" +
                "\n" +
                "Hey, guys! " + Emoji.WAVE_HAND +
                "\n" +
                "Using " + TextFormatter.getBoldString("@" + botUserName) + ", you can find all information about meeting, join to it and find other ones.\n" +
                "Hurry up! There are only " + TextFormatter.getBoldString(meeting.getMeetingInfo().getParticipantLimit()) + " free places! " + Emoji.SCREAM;
    }

    public String getLocationLink(Meeting meeting) {
        Location location = meeting.getLocation();
        String url = String.format(GOOGLE_MAP_LINK_PATTERN, location.getLatitude(), location.getLongitude());
        return TextFormatter.getLink(location.getAddress(), url);
    }
}