package com.razykrashka.bot.stage.meeting.view.utils;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.stage.Stage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

//TODO: Use StringBuilder where string concatenation happens

@Component
public class MeetingMessageUtils {

    public SendMessage createSendMessageWithMeetings(Stage stage, List<Meeting> userMeetings, RazykrashkaBot razykrashkaBot) {
        String meetingsText = createMeetingsText(userMeetings, userMeetings.size());
        SendMessage sendMessage = createSendMessage(razykrashkaBot, meetingsText);
        if (userMeetings.size() > 5) {
            // PAGINATION INLINE KEYBOARD
            // sendMessage.setReplyMarkup(stage.getKeyboard(null));
        }
        return sendMessage;
    }

    public String createMeetingsText(List<Meeting> userMeetings, int meetingAmount) {
        return userMeetings.stream()
                .map(this::createSingleMeetingMainInformationText)
                .collect(Collectors.joining("\n\n", "\uD83D\uDCAB Найдено " + meetingAmount + " встреч(и)\n\n", ""));
    }

    public SendMessage createSendMessageForSingleMeeting(Stage stage, Meeting userMeeting, RazykrashkaBot razykrashkaBot) {
        String meetingText = createSingleMeetingFullText(userMeeting);
        SendMessage sendMessage = createSendMessage(razykrashkaBot, meetingText);

        // sendMessage.setReplyMarkup(stage.getKeyboard(userMeeting));
        return sendMessage;
    }

    public String createSingleMeetingFullText(Meeting meeting) {
        return "<code>MEETING # " + meeting.getId() + "</code>\n" +
                meeting.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm",
                        Locale.ENGLISH)) + "\n" + "\uD83D\uDCCD" + meeting.getLocation().getLocationLink().toString() + "\n"
                + meeting.getMeetingInfo().getSpeakingLevel().toString() + "\n"
                + meeting.getMeetingInfo().getTopic() + "\n"
                + meeting.getMeetingInfo().getQuestions().replace("●", "\n●") + "\n";
    }

    private SendMessage createSendMessage(RazykrashkaBot razykrashkaBot, String messageText) {
        return new SendMessage()
                .setChatId(razykrashkaBot.getRealUpdate().getMessage().getChat().getId())
                .setParseMode("html")
                .setText(messageText);
    }

    public String createSingleMeetingMainInformationText(Meeting meeting) {
        return meeting.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH)) + "\n"
                + "\uD83D\uDCCD" + meeting.getLocation().getLocationLink().toString() + "\n"
                + meeting.getMeetingInfo().getSpeakingLevel().toString() + "\n"
                + meeting.getMeetingInfo().getTopic() + "\n"
                + "INFORMATION: /meeting" + meeting.getId();
    }

	public String createMeetingInfoDuringCreation(Meeting meeting) {
		StringBuilder sb = new StringBuilder();

		if (meeting.getMeetingDateTime() != null) {
			String pattern = "dd MMMM (EEEE)";
			if (meeting.getMeetingDateTime().getHour() != 0) {
				pattern = "dd MMMM (EEEE) ⏰ HH:mm";
			}
			sb.append("DATE: ").append(meeting.getMeetingDateTime()
					.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)));
		}

		if (meeting.getLocation() != null) {
			sb.append("\n\nADDRESS: ").append(meeting.getLocation().getLocationLink());
		}

		if (meeting.getMeetingInfo() != null) {
			sb.append("\n\nLEVEL: ").append(meeting.getMeetingInfo().getSpeakingLevel().getLevel());
		}

		if (meeting.getMeetingInfo() != null && meeting.getMeetingInfo().getParticipantLimit() != null) {
			sb.append("\n\nPARTICIPANT LIMIT: ").append(meeting.getMeetingInfo().getParticipantLimit());
		}

		if (meeting.getMeetingInfo() != null && meeting.getMeetingInfo().getTopic() != null) {
			sb.append("\n\nTOPIC: ").append(meeting.getMeetingInfo().getTopic());
		}

		if (meeting.getMeetingInfo() != null && meeting.getMeetingInfo().getQuestions() != null) {
			sb.append("\n\nQUESTION: \n").append(meeting.getMeetingInfo().getQuestions());
		}
		sb.append("\n\n\n");

		return sb.toString();
	}
}