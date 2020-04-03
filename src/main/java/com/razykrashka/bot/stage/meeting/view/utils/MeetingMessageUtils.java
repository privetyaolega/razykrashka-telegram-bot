package com.razykrashka.bot.stage.meeting.view.utils;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
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
    String lastSunny;

    public String createMeetingsText(List<Meeting> userMeetings, Integer telegramUserId) {
        return userMeetings.stream()
                .map(m -> createSingleMeetingMainInformationText(m, telegramUserId))
                .collect(Collectors.joining("\n\n"));
    }

    public String createSingleMeetingFullInfo(Meeting meeting) {
        lastSunny = Emoji.SMALL_SUN;
        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        StringBuilder sb = new StringBuilder();
        StringBuilder header = new StringBuilder()
                .append(Emoji.NEEDLE).append(Emoji.SPACES).append(TextFormatter.getCodeString(" MEETING # " + meeting.getId())).append("\n\n");
        StringBuilder date = new StringBuilder()
                .append(Emoji.BIG_SUN).append("\n").append(Emoji.SMALL_SUN).append(Emoji.SPACES)
                .append(Emoji.CLOCK).append(" ").append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER)).append("\n");

        StringBuilder location = new StringBuilder();
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            location.append(Emoji.BIG_SUN).append("\n").append(Emoji.SMALL_SUN).append(Emoji.SPACES)
                    .append(Emoji.LOCATION).append(" ").append(getLocationLink(meeting)).append("\n");
        } else {
            location.append(Emoji.BIG_SUN).append("\n").append(Emoji.SMALL_SUN).append(Emoji.SPACES)
                    .append(Emoji.INTERNET).append(" Skype: ")
                    .append(TextFormatter.getCodeString(meeting.getTelegramUser().getSkypeContact()))
                    .append("\n");
        }

        StringBuilder levelLine = new StringBuilder()
                .append(Emoji.BIG_SUN).append("\n").append(Emoji.SMALL_SUN).append(Emoji.SPACES)
                .append(Emoji.HIEROGLYPH).append(" ").append(meetingInfo.getSpeakingLevel().getLevel()).append("\n");

        StringBuilder topicLine = new StringBuilder()
                .append(Emoji.BIG_SUN).append("\n").append(Emoji.SMALL_SUN).append(Emoji.SPACES)
                .append(Emoji.SPEECH_CLOUD).append(" ").append(meetingInfo.getTopic()).append("\n");

        String participants = meeting.getParticipants().stream()
                .map(p -> getSingleStringForParticipantsList(p, meeting))
                .collect(Collectors.joining(""));
        StringBuilder participantsLine = new StringBuilder()
                .append(Emoji.BIG_SUN).append("\n").append(Emoji.SMALL_SUN).append(Emoji.SPACES)
                .append(Emoji.TWO_PERSONS_SILHOUETTE).append(TextFormatter.getItalicString(" " + meeting.getParticipants().size() + " out of "
                        + meeting.getMeetingInfo().getParticipantLimit()))
                .append(participants);

        return sb.append(header)
                .append(date)
                .append(location)
                .append(levelLine)
                .append(topicLine)
                .append(participantsLine).append("\n").append(getNextSunny()).toString();
    }

    public String getSingleMeetingDiscussionInfo(Meeting meeting) {
        StringBuilder sb = new StringBuilder();
        StringBuilder header = new StringBuilder()
                .append(Emoji.NEEDLE).append(Emoji.SPACES)
                .append(TextFormatter.getCodeString(" MEETING # " + meeting.getId())).append("\n\n");
        StringBuilder topic = new StringBuilder()
                .append(Emoji.SPEECH_CLOUD).append(" ")
                .append(TextFormatter.getBoldString(meeting.getMeetingInfo().getTopic())).append("\n\n");
        StringBuilder questions = new StringBuilder()
                .append(" ").append(meeting.getMeetingInfo().getQuestions().replace("●", "\n●")
                        .replaceAll(" +", " ")).append("\n");
        return sb.append(header)
                .append(topic)
                .append(questions).toString();
    }

    private String getSingleStringForParticipantsList(TelegramUser telegramUser, Meeting meeting) {
        String profileLinkTmpl = "https://t.me/%s";
        boolean isUserMeetingOwner = meeting.getTelegramUser() != null && meeting.getTelegramUser()
                .getTelegramId().equals(telegramUser.getTelegramId());
        String ownerLabel = isUserMeetingOwner ? " " + Emoji.CROWN : "";

        String participantName = telegramUser.getFirstName() + " " + telegramUser.getLastName();
        if (!telegramUser.getUserName().isEmpty()) {
            String url = String.format(profileLinkTmpl, telegramUser.getUserName());
            participantName = TextFormatter.getLink(participantName, url);
        }
        return "\n" + getNextSunny() + Emoji.SPACES + " • " + participantName + ownerLabel;
    }

    private String getNextSunny() {
        lastSunny = lastSunny.equals(Emoji.SMALL_SUN) ? Emoji.BIG_SUN : Emoji.SMALL_SUN;
        return lastSunny;
    }

    public String createSingleMeetingMainInformationText(Meeting meeting, Integer telegramUserId) {
        Integer freePlacesAmount = meeting.getMeetingInfo().getParticipantLimit() - meeting.getParticipants().size();

        String freePlacesLine = new StringBuilder()
                .append(Emoji.NEEDLE).append(" ").append(freePlacesAmount)
                .append(TextFormatter.getItalicString(" free places!")).toString();
        String dateLine = new StringBuilder()
                .append(Emoji.SPACES).append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER)).toString();
        StringBuilder locationLine = new StringBuilder();
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            locationLine.append("\n").append(Emoji.SPACES).append(getLocationLink(meeting));
        }

        String levelLine = new StringBuilder()
                .append(Emoji.SPACES).append(TextFormatter.getBoldString(meeting.getMeetingInfo().getSpeakingLevel().getLevel()))
                .toString();
        String topicLevelLine = new StringBuilder()
                .append(Emoji.SPACES).append(Emoji.SPEECH_CLOUD).append(" ").append(meeting.getMeetingInfo().getTopic())
                .toString();

        StringBuilder sb = new StringBuilder()
                .append(freePlacesLine).append("\n")
                .append(dateLine)
                .append(locationLine).append("\n")
                .append(levelLine).append("\n")
                .append(topicLevelLine).append("\n");

        int spacesAmount = (int) ((dateLine.length() - ("/meeting" + meeting.getId()).length()) * 1.55);
        StringBuilder meetingLinkLine = new StringBuilder();
        while (spacesAmount != 0) {
            meetingLinkLine.append(" ");
            spacesAmount--;
        }
        boolean isUserMeetingOwner = (meeting.getTelegramUser() != null && meeting.getTelegramUser().getTelegramId().equals(telegramUserId));
        meetingLinkLine.append(TextFormatter.getBoldString("/meeting" + meeting.getId()))
                .append(isUserMeetingOwner ? " " + Emoji.CROWN : "");
        return sb.append(meetingLinkLine).toString();
    }

    public String createMeetingInfoDuringCreation(Meeting meeting) {

        if (meeting.getFormat().equals(MeetingFormatEnum.ONLINE)) {
            return createMeetingInfoDuringCreationOnline(meeting);
        }


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
                    .append(date);
        }

        if (meeting.getLocation() != null) {
            String locationLink = getLocationLink(meeting);
            sb.append("\n\n")
                    .append(Emoji.LOCATION)
                    .append(TextFormatter.getBoldString(" Address: "))
                    .append(locationLink);
        }

        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        if (meetingInfo != null) {
            if (meetingInfo.getSpeakingLevel() != null) {
                sb.append("\n\n")
                        .append(Emoji.HIEROGLYPH)
                        .append(TextFormatter.getBoldString(" Level: "))
                        .append(meetingInfo.getSpeakingLevel().getLevel());
            }
            if (meetingInfo.getParticipantLimit() != null) {
                sb.append("\n\n")
                        .append(Emoji.TWO_PERSONS_SILHOUETTE)
                        .append(TextFormatter.getBoldString(" Participant limit: "))
                        .append(meetingInfo.getParticipantLimit());
            }
            if (meetingInfo.getTopic() != null) {
                sb.append("\n\n")
                        .append(Emoji.NEEDLE)
                        .append(TextFormatter.getBoldString(" Topic: "))
                        .append(meetingInfo.getTopic());
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

    public String createMeetingInfoDuringCreationOnline(Meeting meeting) {
        StringBuilder sb = new StringBuilder()
                .append(Emoji.INTERNET).append(" ONLINE MEETING\n\n");

        if (meeting.getMeetingDateTime() != null) {
            String pattern = DATE_PATTERN;
            if (meeting.getMeetingDateTime().getHour() != 0) {
                pattern = DATE_TIME_PATTERN;
            }
            String date = meeting.getMeetingDateTime()
                    .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
            sb.append(Emoji.CLOCK)
                    .append(TextFormatter.getBoldString(" Date: "))
                    .append(date);
        }

        if (meeting.getTelegramUser().getSkypeContact() != null) {
            sb.append("\n\n")
                    .append(Emoji.PHONE_V1)
                    .append(TextFormatter.getBoldString(" Skype: "))
                    .append(TextFormatter.getCodeString(meeting.getTelegramUser().getSkypeContact()));
        }

        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        if (meetingInfo != null) {
            if (meetingInfo.getSpeakingLevel() != null) {
                sb.append("\n\n")
                        .append(Emoji.HIEROGLYPH)
                        .append(TextFormatter.getBoldString(" Level: "))
                        .append(meetingInfo.getSpeakingLevel().getLevel());
            }
            if (meetingInfo.getParticipantLimit() != null) {
                sb.append("\n\n")
                        .append(Emoji.TWO_PERSONS_SILHOUETTE)
                        .append(TextFormatter.getBoldString(" Participant limit: "))
                        .append(meetingInfo.getParticipantLimit());
            }
            if (meetingInfo.getTopic() != null) {
                sb.append("\n\n")
                        .append(Emoji.NEEDLE)
                        .append(TextFormatter.getBoldString(" Topic: "))
                        .append(meetingInfo.getTopic());
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
                .append(Emoji.SMALL_SUN).append("   ").append(Emoji.TWO_PERSONS_SILHOUETTE).append("   ").append(meeting.getMeetingInfo().getParticipantLimit()).append("\n")
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