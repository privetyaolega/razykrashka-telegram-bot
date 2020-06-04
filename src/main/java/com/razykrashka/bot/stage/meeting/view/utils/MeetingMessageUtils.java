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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    final static String DATE_TIME_PATTERN = "dd MMMM (eee) HH:mm";
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

        header.append(Emoji.RADIO_BUTTON).append(Emoji.SPACES);
        String meetingHeaderName = meeting.getFormat().equals(MeetingFormatEnum.ONLINE) ? "Oɴʟɪɴᴇ ᴍᴇᴇᴛɪɴɢ # " : "Oғғʟɪɴᴇ ᴍᴇᴇᴛɪɴɢ # ";
//                .append(Emoji.SPACES).append(Emoji.SPACES).append(Emoji.SPACES).append(Emoji.SPACES)
        header.append(TextFormatter.getCodeString(meetingHeaderName + meeting.getId()))
                .append("\n").append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("\n");

        StringBuilder date = new StringBuilder()
                .append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                .append(Emoji.CLOCK).append(" ").append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER))
                .append(" • ").append(getTimeBefore(meeting))
                .append("\n");

        StringBuilder location = new StringBuilder();
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            location.append(Emoji.CHAINS).append("\n").append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                    .append(Emoji.LOCATION).append(" ").append(getLocationLink(meeting)).append("\n");
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

        sb.append(header)
                .append(date)
                .append(location)
                .append(levelLine)
                .append(topicLine)
                .append(participantsLine).append("\n").append(Emoji.RADIO_BUTTON);
        if (meeting.getFormat().equals(MeetingFormatEnum.ONLINE)) {
            sb.append("\n\nA discord channel for this meeting will be created <b>automatically</b> and all related information " +
                    "will be sent to all the participants by our bot <b>ONE hour before the start</b> ☺️\n\nGood luck! " + Emoji.SHAMROCK);
        }

        return sb.toString();
    }

    public String createArchivedMeetingFullInfo(Meeting meeting) {
        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        StringBuilder sb = new StringBuilder();
        StringBuilder header = new StringBuilder();

        header.append(Emoji.RADIO_BUTTON).append(Emoji.SPACES);
        header.append(TextFormatter.getCodeString("Aʀᴄʜɪᴠᴇᴅ ᴍᴇᴇᴛɪɴɢ #" + meeting.getId()))
                .append("\n").append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("\n");

        StringBuilder date = new StringBuilder()
                .append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                .append(Emoji.CLOCK).append(" ").append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER))
                .append("\n").append(Emoji.CHAINS).append(Emoji.SPACES).append(Emoji.SPACES)
                .append(getTimeBefore(meeting)
                        .replace("In", "Was")
                        .replace("-", "")).append(" ago")
                .append("\n");

        StringBuilder location = new StringBuilder();
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            location.append(Emoji.CHAINS).append("\n").append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
                    .append(Emoji.LOCATION).append(" ").append(getLocationLink(meeting)).append("\n");
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

        sb.append(header)
                .append(date)
                .append(location)
                .append(levelLine)
                .append(topicLine)
                .append(participantsLine).append("\n").append(Emoji.RADIO_BUTTON);

        return sb.toString();
    }


    public String getSingleMeetingDiscussionInfo(Meeting meeting) {
        StringBuilder sb = new StringBuilder();
        StringBuilder header = new StringBuilder()
                .append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
//                .append(Emoji.SPACES).append(Emoji.SPACES).append(Emoji.SPACES).append(Emoji.SPACES)
//                .append(Emoji.SPACES).append(Emoji.LIGHTNING)
//                .append(TextFormatter.getCodeString(" MEETING # " + meeting.getId())).append("\n")
                .append(TextFormatter.getCodeString("Mᴇᴇᴛɪɴɢ # " + meeting.getId())).append("\n")
                .append(Emoji.CHAINS).append("\n");
        StringBuilder topic = new StringBuilder()
                .append(Emoji.RADIO_BUTTON).append(Emoji.SPACES)
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
            sb.append("\n<b>").append(i + 1).append(")</b> ").append(questionsArray[i]);
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
            freePlacesLine.append(Emoji.NEEDLE).append(" ")
                    .append(TextFormatter.getItalicString(freePlacesAmount + " free place(s)"));
        } else {
            freePlacesLine.append(Emoji.NEEDLE)
                    .append(" ")
                    .append(TextFormatter.getItalicString("No free places! "))
                    .append(Emoji.NO_ENTRY_SIGN);
        }

        String dateLine = new StringBuilder()
                .append(Emoji.SPACES).append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER))
                .append(" • ").append(getTimeBefore(meeting))
                .toString();
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

        int spacesAmount = (int) ((dateLine.length() * 1.2 - ("/meeting" + meeting.getId()).length()));
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
            freePlacesLine.append(formatLabel).append(" ")
                    .append(TextFormatter.getItalicString(freePlacesAmount + " free place(s)"));
        } else {
            freePlacesLine.append(formatLabel)
                    .append(" ")
                    .append(TextFormatter.getItalicString("No free places! "))
                    .append(Emoji.NO_ENTRY_SIGN);
        }

        String dateLine = new StringBuilder()
                .append(Emoji.SPACES).append(meeting.getMeetingDateTime().format(DATE_TIME_FORMATTER))
                .append(" • ").append(getTimeBefore(meeting))
                .toString();
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

        int spacesAmount = (int) ((dateLine.length() * 1.2 - ("/meeting" + meeting.getId()).length()));
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
        int spacesAmount = (int) ((dateLine.length() * 1.2 - ("/meeting" + m.getId()).length()));
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
//                .append(Emoji.COFFEE).append(" Oғғʟɪɴᴇ ᴍᴇᴇᴛɪɴɢ\n\n");

        if (meeting.getMeetingDateTime() != null) {
            String pattern = DATE_TIME_PATTERN;
            if (meeting.getMeetingDateTime().getHour() == 0
                    && meeting.getMeetingDateTime().getMinute() == 0) {
                pattern = DATE_PATTERN;
            }
            String date = meeting.getMeetingDateTime()
                    .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
            sb.append(Emoji.CLOCK)
                    .append(TextFormatter.getBoldString(" Dᴀᴛᴇ: "))
                    .append(date);
        }

        if (meeting.getLocation() != null) {
            String locationLink = getLocationLink(meeting);
            sb.append("\n\n")
                    .append(Emoji.LOCATION)
                    .append(TextFormatter.getBoldString(" Aᴅᴅʀᴇss: "))
                    .append(locationLink);
        }

        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        if (meetingInfo != null) {
            if (meetingInfo.getSpeakingLevel() != null) {
                sb.append("\n\n")
                        .append(Emoji.HIEROGLYPH)
                        .append(TextFormatter.getBoldString(" Lᴇᴠᴇʟ: "))
                        .append(meetingInfo.getSpeakingLevel().getLabel());
            }
            if (meetingInfo.getParticipantLimit() != null) {
                sb.append("\n\n")
                        .append(Emoji.TWO_PERSONS_SILHOUETTE)
                        .append(TextFormatter.getBoldString(" Pᴀʀᴛɪᴄɪᴘᴀɴᴛ ʟɪᴍɪᴛ: "))
                        .append(meetingInfo.getParticipantLimit());
            }
            if (meetingInfo.getTopic() != null) {
                sb.append("\n\n")
                        .append(Emoji.NEEDLE)
                        .append(TextFormatter.getBoldString(" Tᴏᴘɪᴄ: "))
                        .append(meetingInfo.getTopic());
            }
            if (meetingInfo.getQuestions() != null) {
                sb.append("\n\n")
                        .append(Emoji.SPEECH_CLOUD)
                        .append(TextFormatter.getBoldString(" Qᴜᴇsᴛɪᴏɴs: \n"))
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
                .append(Emoji.INTERNET).append(" Oɴʟɪɴᴇ ᴍᴇᴇᴛɪɴɢ\n\n");

        if (meeting.getMeetingDateTime() != null) {
            String pattern = DATE_TIME_PATTERN;
            if (meeting.getMeetingDateTime().getHour() == 0
                    && meeting.getMeetingDateTime().getMinute() == 0) {
                pattern = DATE_PATTERN;
            }
            String date = meeting.getMeetingDateTime()
                    .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
            sb.append(Emoji.CLOCK)
                    .append(TextFormatter.getBoldString(" Dᴀᴛᴇ: "))
                    .append(date);
        }

        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        if (meetingInfo != null) {
            if (meetingInfo.getSpeakingLevel() != null) {
                sb.append("\n\n")
                        .append(Emoji.HIEROGLYPH)
                        .append(TextFormatter.getBoldString(" Lᴇᴠᴇʟ: "))
                        .append(meetingInfo.getSpeakingLevel().getLabel());
            }
            if (meetingInfo.getParticipantLimit() != null) {
                sb.append("\n\n")
                        .append(Emoji.TWO_PERSONS_SILHOUETTE)
                        .append(TextFormatter.getBoldString(" Pᴀʀᴛɪᴄɪᴘᴀɴᴛ ʟɪᴍɪᴛ: "))
                        .append(meetingInfo.getParticipantLimit());
            }
            if (meetingInfo.getTopic() != null) {
                sb.append("\n\n")
                        .append(Emoji.NEEDLE)
                        .append(TextFormatter.getBoldString(" Tᴏᴘɪᴄ: "))
                        .append(meetingInfo.getTopic());
            }
            if (meetingInfo.getQuestions() != null) {
                sb.append("\n\n")
                        .append(Emoji.SPEECH_CLOUD)
                        .append(TextFormatter.getBoldString(" Qᴜᴇsᴛɪᴏɴs: \n"))
                        .append(questionsToPrettyBulletedString(meetingInfo.getQuestions()));
            }
        }
        return sb.append("\n\n\n").toString();
    }

    public String createMeetingInfoGroup(Meeting meeting) {
        StringBuilder sb = new StringBuilder().append("⠀⠀⠀").append(Emoji.FIRE);
        String meetingHeaderName = meeting.getFormat().equals(MeetingFormatEnum.ONLINE) ?
                "  Nᴇᴡ ᴏɴʟɪɴᴇ ᴍᴇᴇᴛɪɴɢ # " : "  Nᴇᴡ ᴏғғʟɪɴᴇ ᴍᴇᴇᴛɪɴɢ # ";
        sb.append(TextFormatter.getBoldString(meetingHeaderName + meeting.getId() + "  "))
                .append(Emoji.FIRE).append(Emoji.SPACES).append(Emoji.SPACES).append("\n\n")
                .append(Emoji.RADIO_BUTTON).append("\n")
                .append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("   ").append(Emoji.CLOCK).append("   ")
                .append(meeting.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH))).append("\n");

        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            sb.append(Emoji.CHAINS).append("\n")
                    .append(Emoji.CHAINS).append("   ").append(Emoji.LOCATION).append("   ")
                    .append(getLocationLink(meeting)).append("\n");
        }

        sb.append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("   ").append(Emoji.TWO_PERSONS_SILHOUETTE).append("   ").append(meeting.getMeetingInfo().getParticipantLimit()).append("\n")
                .append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("   ").append(Emoji.HIEROGLYPH).append("   ").append(meeting.getMeetingInfo().getSpeakingLevel().getLabel()).append("\n")
                .append(Emoji.CHAINS).append("\n")
                .append(Emoji.CHAINS).append("   ").append(Emoji.SPEECH_CLOUD).append("   ").append(meeting.getMeetingInfo().getTopic()).append("\n")
                .append(Emoji.CHAINS).append("\n")
                .append(Emoji.RADIO_BUTTON).append("\n\n\n")
                .append("Hey, guys! ").append(Emoji.WAVE_HAND).append("\n")
                .append("A new meeting is available! Check it out now and join! ")
//                .append("\n\n").append("You can find out all the details and join the meeting using ").append(TextFormatter.getBoldString("@" + botUserName))
                .append("\n\nHurry up! There are only ").append(TextFormatter.getBoldString(meeting.getMeetingInfo().getParticipantLimit() - 1))
                .append(" place(s) left! ").append(Emoji.SCREAM)

//                .append("\n\n").append(Emoji.WARNING)
//                .append("\n<b>Sorry, but our bot is still in the test mode\nNone of the meetings are valid yet.\nWe want to make it better for you and perfection takes time!</b>\n")
//                .append(Emoji.WARNING)

                .toString();

        return sb.toString();
    }

    public String getLocationLink(Meeting meeting) {
        Location location = meeting.getLocation();
        String url = String.format(GOOGLE_MAP_LINK_PATTERN, location.getLatitude(), location.getLongitude());
        return TextFormatter.getLink(location.getAddress(), url);
    }

    private String getTimeBefore(Meeting m) {
        LocalDateTime ldt1 = LocalDateTime.now();
        LocalDateTime ldt2 = m.getMeetingDateTime();
        long minuteBeforeMeeting = Duration.between(ldt1, ldt2).toMinutes();
        String message;

        if (m.getMeetingDateTime().toLocalDate().isEqual(LocalDate.now())) {
            if (minuteBeforeMeeting < 60) {
                message = "In " + minuteBeforeMeeting + " min(s)" + Emoji.FIRE;
            } else {
                long hours = minuteBeforeMeeting / 60;
                message = "In " + hours + "h " + minuteBeforeMeeting % 60 + "m";
                if (hours < 2) {
                    message += " " + Emoji.FIRE;
                }
            }
        } else {
            long days = minuteBeforeMeeting / 1440;
            message = "In " + days + "d " + (minuteBeforeMeeting % 1440) / 60 + "h";
        }
        return TextFormatter.getItalicString(message);
    }
}