package com.razykrashka.bot.stage.meeting.view.utils;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    UpdateHelper updateHelper;
    @Value("${razykrashka.bot.username}")
    String botUserName;
    final static String GOOGLE_MAP_LINK_PATTERN = "https://www.google.com/maps/search/?api=1&query=%s,%s";
    final static String DATE_TIME_PATTERN = "dd MMMM (EEEE) HH:mm";
    final static String DATE_PATTERN = "dd MMMM (EEEE)";
    final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.ENGLISH);

    public String getPaginationAllGeneral(List<Meeting> userMeetings) {
        return userMeetings.stream()
                .map(this::createSingleMeetingMainInformationText)
                .collect(Collectors.joining("\n\n"));
    }

    public String getPaginationAllViewArchived(List<Meeting> userMeetings) {
        return userMeetings.stream()
                .map(this::getPaginationSingleViewArchived)
                .collect(Collectors.joining("\n\n"));
    }

    public String getPaginationAllViewActive(List<Meeting> userMeetings) {
        return userMeetings.stream()
                .map(this::getPaginationSingleViewActive)
                .collect(Collectors.joining("\n\n"));
    }

    public String createSingleMeetingFullInfo(Meeting meeting) {
        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        StringBuilder sb = new StringBuilder();
        StringBuilder header = new StringBuilder();

        int freePlacesAmount = meeting.getMeetingInfo().getParticipantLimit() - meeting.getParticipants().size();
        if (freePlacesAmount == 0) {
            header.append(Emoji.ANGER).append(Emoji.ANGER).append(Emoji.ANGER).append(Emoji.ANGER)
                    .append(TextFormatter.getItalicString(" NO FREE PLACES! "))
                    .append(Emoji.ANGER).append(Emoji.ANGER).append(Emoji.ANGER).append(Emoji.ANGER)
                    .append("\n\n");
        }

        header.append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                .append(Emoji.SPACES).append(Emoji.SPACES).append(Emoji.SPACES).append(Emoji.SPACES)
                .append(Emoji.SPACES).append(Emoji.LIGHTNING)
                .append(TextFormatter.getCodeString(" MEETING # " + meeting.getId()))
                .append("\n").append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("\n");

        StringBuilder date = new StringBuilder()
                .append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                .append(Emoji.CLOCK).append(" ").append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER)).append("\n");

        StringBuilder location = new StringBuilder();
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            location.append(Emoji.CHAINS).append("\n").append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                    .append(Emoji.LOCATION).append(" ").append(getLocationLink(meeting)).append("\n");
        } else {
            location.append(Emoji.CHAINS).append("\n").append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                    .append(Emoji.INTERNET).append(" Skype: ")
                    .append(TextFormatter.getCodeString(meeting.getTelegramUser().getSkypeContact()))
                    .append("\n");
        }

        StringBuilder levelLine = new StringBuilder()
                .append(Emoji.CHAINS).append("\n").append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                .append(Emoji.HIEROGLYPH).append(" ").append(meetingInfo.getSpeakingLevel().getLabel()).append("\n");

        StringBuilder topicLine = new StringBuilder()
                .append(Emoji.CHAINS).append("\n").append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                .append(Emoji.SPEECH_CLOUD).append(" ").append(meetingInfo.getTopic()).append("\n");

        String participants = meeting.getParticipants().stream()
                .map(p -> getSingleStringForParticipantsList(p, meeting))
                .collect(Collectors.joining(""));
        StringBuilder participantsLine = new StringBuilder()
                .append(Emoji.CHAINS).append("\n").append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                .append(Emoji.TWO_PERSONS_SILHOUETTE).append(TextFormatter.getItalicString(" " + meeting.getParticipants().size() + " out of "
                        + meeting.getMeetingInfo().getParticipantLimit()))
                .append(participants);

        return sb.append(header)
                .append(date)
                .append(location)
                .append(levelLine)
                .append(topicLine)
                .append(participantsLine).append("\n").append(Emoji.RADIO_BUTTON).toString();
    }

    public String getSingleMeetingDiscussionInfo(Meeting meeting) {
        StringBuilder sb = new StringBuilder();
        StringBuilder header = new StringBuilder()
                .append(Emoji.RADIO_BUTTON).append(Emoji.SPACES).append(Emoji.SPACES)
                .append(Emoji.SPACES).append(Emoji.SPACES).append(Emoji.SPACES)
                .append(Emoji.SPACES).append(Emoji.LIGHTNING)
                .append(TextFormatter.getCodeString(" MEETING # " + meeting.getId())).append("\n")
                .append(Emoji.CHAINS).append("\n");
        StringBuilder topic = new StringBuilder()
                .append(Emoji.RADIO_BUTTON).append(" ")
                .append(TextFormatter.getBoldString(meeting.getMeetingInfo().getTopic())).append("\n");
        String questions = questionsToPrettyOrderedString(meeting.getMeetingInfo().getQuestions());
        return sb.append(header)
                .append(topic)
                .append(questions).toString();
    }

    private String questionsToPrettyOrderedString(String questions) {
        StringBuilder sb = new StringBuilder();
        String[] questionsArray = questions.split(";");
        for (int i = 0; i < questionsArray.length; i++) {
            sb.append("\n").append(i + 1).append(") ").append(questionsArray[i]);
        }
        return sb.toString();
    }

    private String getSingleStringForParticipantsList(TelegramUser telegramUser, Meeting meeting) {
        boolean isUserMeetingOwner = meeting.getTelegramUser() != null
                && meeting.getTelegramUser().getId().equals(telegramUser.getId());
        String ownerLabel = isUserMeetingOwner ? " " + Emoji.CROWN : "";
        return "\n" + Emoji.CHAINS + Emoji.SPACES + " • " + TextFormatter.getTelegramLink(telegramUser) + ownerLabel;
    }

    public String createSingleMeetingMainInformationText(Meeting meeting) {
        int freePlacesAmount = meeting.getMeetingInfo().getParticipantLimit() - meeting.getParticipants().size();

        StringBuilder freePlacesLine = new StringBuilder();

        if (freePlacesAmount != 0) {
            freePlacesLine.append(Emoji.NEEDLE).append(" ").append(freePlacesAmount)
                    .append(TextFormatter.getItalicString(" free places!"));
        } else {
            freePlacesLine.append(Emoji.NEEDLE)
                    .append(" ")
                    .append(TextFormatter.getItalicString("No free places! "))
                    .append(Emoji.NO_ENTRY_SIGN);
        }

        String dateLine = new StringBuilder()
                .append(Emoji.SPACES).append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER)).toString();
        StringBuilder locationLine = new StringBuilder();
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            locationLine.append("\n").append(Emoji.SPACES).append(getLocationLink(meeting));
        }

        String levelLine = new StringBuilder()
                .append(Emoji.SPACES).append(TextFormatter.getBoldString(meeting.getMeetingInfo().getSpeakingLevel().getLabel()))
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

        int spacesAmount = (int) ((dateLine.length() * 1.425 - ("/meeting" + meeting.getId()).length()));
        StringBuilder meetingLinkLine = new StringBuilder();
        while (spacesAmount != 0) {
            meetingLinkLine.append(" ");
            spacesAmount--;
        }
        boolean isUserMeetingOwner = meeting.getTelegramUser().equals(updateHelper.getUser());
        meetingLinkLine.append(TextFormatter.getBoldString("/meeting" + meeting.getId()))
                .append(isUserMeetingOwner ? " " + Emoji.CROWN : "");
        return sb.append(meetingLinkLine).toString();
    }

    private String getPaginationSingleViewArchived(Meeting meeting) {
        String formatLabel = Emoji.COFFEE;
        if (meeting.getFormat().equals(MeetingFormatEnum.ONLINE)) {
            formatLabel = Emoji.INTERNET;
        }

        String dateLine = new StringBuilder()
                .append(formatLabel)
                .append(" ")
                .append(meeting.getMeetingDateTime().format(DateTimeFormatter
                        .ofPattern("dd MMMM yyyy", Locale.ENGLISH))).toString();
        StringBuilder locationLine = new StringBuilder();
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            locationLine.append("\n").append(Emoji.SPACES).append(getLocationLink(meeting));
        }

        String levelLine = new StringBuilder()
                .append(Emoji.SPACES).append(TextFormatter.getBoldString(meeting.getMeetingInfo().getSpeakingLevel().getLabel()))
                .toString();
        String topicLevelLine = new StringBuilder()
                .append(Emoji.SPACES).append(Emoji.SPEECH_CLOUD).append(" ").append(meeting.getMeetingInfo().getTopic())
                .toString();

        StringBuilder sb = new StringBuilder()
                .append(dateLine)
                .append(locationLine).append("\n")
                .append(levelLine).append("\n")
                .append(topicLevelLine).append("\n");

        return sb.append(getMeetingLine(meeting, dateLine)).toString();
    }

    private String getPaginationSingleViewActive(Meeting meeting) {
        int freePlacesAmount = meeting.getMeetingInfo().getParticipantLimit() - meeting.getParticipants().size();

        StringBuilder freePlacesLine = new StringBuilder();

        String formatLabel = Emoji.COFFEE;
        if (meeting.getFormat().equals(MeetingFormatEnum.ONLINE)) {
            formatLabel = Emoji.INTERNET;
        }

        if (freePlacesAmount != 0) {
            freePlacesLine.append(formatLabel).append(" ").append(freePlacesAmount)
                    .append(TextFormatter.getItalicString(" free places!"));
        } else {
            freePlacesLine.append(formatLabel)
                    .append(" ")
                    .append(TextFormatter.getItalicString("No free places! "))
                    .append(Emoji.NO_ENTRY_SIGN);
        }

        String dateLine = new StringBuilder()
                .append(Emoji.SPACES).append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER)).toString();
        StringBuilder locationLine = new StringBuilder();
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            locationLine.append("\n").append(Emoji.SPACES).append(getLocationLink(meeting));
        }

        String levelLine = new StringBuilder()
                .append(Emoji.SPACES).append(TextFormatter.getBoldString(meeting.getMeetingInfo().getSpeakingLevel().getLabel()))
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

        int spacesAmount = (int) ((dateLine.length() * 1.425 - ("/meeting" + meeting.getId()).length()));
        StringBuilder meetingLinkLine = new StringBuilder();
        while (spacesAmount != 0) {
            meetingLinkLine.append(" ");
            spacesAmount--;
        }
        boolean isUserMeetingOwner = meeting.getTelegramUser().equals(updateHelper.getUser());
        meetingLinkLine.append(TextFormatter.getBoldString("/meeting" + meeting.getId()))
                .append(isUserMeetingOwner ? " " + Emoji.CROWN : "");
        return sb.append(meetingLinkLine).toString();
    }

    private String getMeetingLine(Meeting m, String dateLine) {
        int spacesAmount = (int) ((dateLine.length() * 1.52 - ("/meeting" + m.getId()).length()));
        StringBuilder meetingLinkLine = new StringBuilder();
        while (spacesAmount != 0) {
            meetingLinkLine.append(" ");
            spacesAmount--;
        }

        boolean isUserMeetingOwner = m.getTelegramUser().equals(updateHelper.getUser());
        return meetingLinkLine.append(TextFormatter.getBoldString("/meeting" + m.getId()))
                .append(isUserMeetingOwner ? " " + Emoji.CROWN : "").toString();
    }

    public String createMeetingInfoDuringCreation(Meeting meeting) {
        if (meeting.getFormat().equals(MeetingFormatEnum.ONLINE)) {
            return createMeetingInfoDuringCreationOnline(meeting);
        }

        StringBuilder sb = new StringBuilder();

        if (meeting.getMeetingDateTime() != null) {
            String pattern = DATE_TIME_PATTERN;
            if (meeting.getMeetingDateTime().getHour() == 0
                    && meeting.getMeetingDateTime().getMinute() == 0) {
                pattern = DATE_PATTERN;
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
                        .append(meetingInfo.getSpeakingLevel().getLabel());
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
                        .append(questionsToPrettyBulletedString(meetingInfo.getQuestions()));
            }
        }
        return sb.append("\n\n\n").toString();
    }

    private String questionsToPrettyBulletedString(String questions) {
        StringBuilder sb = new StringBuilder();
        String[] questionsArray = questions.split(";");
        for (int i = 0; i < questionsArray.length; i++) {
            sb.append("\n").append(" • ").append(questionsArray[i]);
        }
        return sb.toString();
    }

    public String createMeetingInfoDuringCreationOnline(Meeting meeting) {
        StringBuilder sb = new StringBuilder()
                .append(Emoji.INTERNET).append(" Online meeting\n\n");

        if (meeting.getMeetingDateTime() != null) {
            String pattern = DATE_TIME_PATTERN;
            if (meeting.getMeetingDateTime().getHour() == 0
                    && meeting.getMeetingDateTime().getMinute() == 0) {
                pattern = DATE_PATTERN;
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
                        .append(meetingInfo.getSpeakingLevel().getLabel());
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
                        .append(questionsToPrettyBulletedString(meetingInfo.getQuestions()));
            }
        }
        return sb.append("\n\n\n").toString();
    }

    public String createMeetingInfoGroup(Meeting meeting) {
        StringBuilder sb = new StringBuilder().append(Emoji.FIRE).append(Emoji.FIRE).append(Emoji.FIRE)
                .append(TextFormatter.getBoldString("  NEW MEETING # " + meeting.getId() + "  "))
                .append(Emoji.FIRE).append(Emoji.FIRE).append(Emoji.FIRE).append("\n\n")
                .append(Emoji.RADIO_BUTTON).append("\n")
                .append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("   ").append(Emoji.CLOCK).append("   ")
                .append(meeting.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH))).append("\n")
                .append(Emoji.CHAINS).append("\n");

        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            sb.append(Emoji.CHAINS).append("   ").append(Emoji.LOCATION).append("   ")
                    .append(getLocationLink(meeting)).append("\n");
        } else {
            sb.append(Emoji.CHAINS).append("   ").append(Emoji.INTERNET).append("   ")
                    .append(" ONLINE").append("\n");
        }

        sb.append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("   ").append(Emoji.TWO_PERSONS_SILHOUETTE).append("   ").append(meeting.getMeetingInfo().getParticipantLimit()).append("\n")
                .append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("   ").append(Emoji.HIEROGLYPH).append("   ").append(meeting.getMeetingInfo().getSpeakingLevel().getLabel()).append("\n")
                .append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("   ").append(Emoji.SPEECH_CLOUD).append("   ").append(meeting.getMeetingInfo().getTopic()).append("\n")
                .append(Emoji.CHAINS).append("\n")
                .append(Emoji.RADIO_BUTTON).append("\n").append("\n")
                .append("Hey, guys! ").append(Emoji.WAVE_HAND).append("\n")
                .append("A new meeting is available Hurry up to check it and join. ").append("\n").append("\n")
                .append("Using ").append(TextFormatter.getBoldString("@" + botUserName))
                .append(", you can find all information about meeting, join to it and find other ones.\n")
                .append("Hurry up! There are only ").append(TextFormatter.getBoldString(meeting.getMeetingInfo().getParticipantLimit() - 1))
                .append(" places left! ").append(Emoji.SCREAM).toString();

        return sb.toString();
    }

    public String getLocationLink(Meeting meeting) {
        Location location = meeting.getLocation();
        String url = String.format(GOOGLE_MAP_LINK_PATTERN, location.getLatitude(), location.getLongitude());
        return TextFormatter.getLink(location.getAddress(), url);
    }
}