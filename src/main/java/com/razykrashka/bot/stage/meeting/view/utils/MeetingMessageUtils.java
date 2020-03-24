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
    final static String DATE_TIME_PATTERN = "dd MMMM (EEEE) HH:mm";
    final static String DATE_PATTERN = "dd MMMM (EEEE)";
    final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.ENGLISH);

    public String createMeetingsText(List<Meeting> userMeetings, Integer telegramUserId) {
        return userMeetings.stream()
                .map(m -> createSingleMeetingMainInformationText(m, telegramUserId))
                .collect(Collectors.joining("\n\n"));
    }

    public String createSingleMeetingFullText(Meeting meeting) {
        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        return Emoji.LIGHTNING + TextFormatter.getCodeString(" MEETING # " + meeting.getId()) + "\n\n" +
                meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER) + "\n"
                + Emoji.LOCATION + getLocationLink(meeting) + "\n\n"
                + meetingInfo.getTopic() + " " + Emoji.SPEECH_CLOUD + " " + meetingInfo.getSpeakingLevel().getLevel() + "\n"
                + meetingInfo.getQuestions().replace("●", "\n●")
                .replaceAll(" +", " ") + "\n";
    }

    public String createSingleMeetingMainInformationText(Meeting meeting, Integer telegramUserId) {
        return new StringBuilder()
                .append(Emoji.NEEDLE).append(" ").append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER)).append("\n")
                .append(getLocationLink(meeting)).append("\n")
                .append(meeting.getMeetingInfo().getTopic()).append(" ").append(Emoji.SPEECH_CLOUD).append(" ").append("(")
                .append(meeting.getMeetingInfo().getSpeakingLevel().getLevel()).append(")\n")
                .append(Emoji.INFORMATION).append(TextFormatter.getBoldString("                                  /meeting" + meeting.getId()))
                .append(meeting.getTelegramUser().getTelegramId().equals(telegramUserId) ? " " + Emoji.CROWN : "")
                .toString();
    }

    public String createMeetingInfoDuringCreation(Meeting meeting) {
        StringBuilder sb = new StringBuilder();

        if (meeting.getMeetingDateTime() != null) {
            String pattern = DATE_PATTERN;
            if (meeting.getMeetingDateTime().getHour() != 0) {
                pattern = DATE_TIME_PATTERN;
            }
            String date = meeting.getMeetingDateTime()
                    .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
            sb.append(Emoji.CLOCK)
                    .append(TextFormatter.getBoldString(" Date: "))
                    .append(TextFormatter.getFramedString(date));
        }

        if (meeting.getLocation() != null) {
            String locationLink = getLocationLink(meeting);
            sb.append("\n\n")
                    .append(Emoji.LOCATION)
                    .append(TextFormatter.getBoldString(" Address: "))
                    .append(TextFormatter.getFramedString(locationLink));
        }

        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        if (meetingInfo != null) {
            if (meetingInfo.getSpeakingLevel() != null) {
                sb.append("\n\n")
                        .append(Emoji.HIEROGLYPH)
                        .append(TextFormatter.getBoldString(" Level: "))
                        .append(TextFormatter.getFramedString(meetingInfo.getSpeakingLevel().getLevel()));
            }
            if (meetingInfo.getParticipantLimit() != null) {
                sb.append("\n\n")
                        .append(Emoji.PEOPLE)
                        .append(TextFormatter.getBoldString(" Participant limit: "))
                        .append(TextFormatter.getFramedString(meetingInfo.getParticipantLimit()));
            }
            if (meetingInfo.getTopic() != null) {
                sb.append("\n\n")
                        .append(Emoji.NEEDLE)
                        .append(TextFormatter.getBoldString(" Topic: "))
                        .append(TextFormatter.getFramedString(meetingInfo.getTopic()));
            }
            if (meetingInfo.getQuestions() != null) {
                sb.append("\n\n")
                        .append(Emoji.SPEECH_CLOUD)
                        .append(TextFormatter.getBoldString(" Questions: \n"))
                        .append(meetingInfo.getQuestions());
            }
        }
        return sb.append("\n\n\n").toString();
    }

    public String createMeetingInfoGroup(Meeting meeting) {
        return new StringBuilder().append(Emoji.FIRE).append(" NEW MEETING! ").append(Emoji.FIRE).append("\n").append("\n")
                .append(Emoji.BIG_SUN).append("\n")
                .append(Emoji.SMALL_SUN).append("   ").append(Emoji.CLOCK).append("   ")
                .append(meeting.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH))).append("\n")
                .append(Emoji.BIG_SUN).append("\n")
                .append(Emoji.SMALL_SUN).append("   ").append(Emoji.LOCATION).append("   ").append(getLocationLink(meeting)).append("\n")
                .append(Emoji.BIG_SUN).append("\n")
                .append(Emoji.SMALL_SUN).append("   ").append(Emoji.PEOPLE).append("   ").append(meeting.getMeetingInfo().getParticipantLimit()).append("\n")
                .append(Emoji.BIG_SUN).append("\n")
                .append(Emoji.SMALL_SUN).append("   ").append(Emoji.SPEECH_CLOUD).append("   ").append(meeting.getMeetingInfo().getTopic()).append(" (").append(meeting.getMeetingInfo().getSpeakingLevel().getLevel()).append(")").append("\n")
                .append(Emoji.BIG_SUN).append("\n").append("\n")
                .append("Hey, guys! ").append(Emoji.WAVE_HAND).append("\n")
                .append("Using ").append(TextFormatter.getBoldString("@" + botUserName))
                .append(", you can find all information about meeting, join to it and find other ones.\n")
                .append("Hurry up! There are only ").append(TextFormatter.getBoldString(meeting.getMeetingInfo().getParticipantLimit() - 1))
                .append(" free places! ").append(Emoji.SCREAM).toString();
    }

    public String getLocationLink(Meeting meeting) {
        Location location = meeting.getLocation();
        String url = String.format(GOOGLE_MAP_LINK_PATTERN, location.getLatitude(), location.getLongitude());
        return TextFormatter.getLink(location.getAddress(), url);
    }
}