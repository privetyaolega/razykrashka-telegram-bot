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

    private String createSingleMeetingFullText(Meeting meeting) {
        return "<code>MEETING # " + meeting.getId() + "</code>\n" +
                meeting.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm",
                        Locale.ENGLISH)) + "\n" + "\uD83D\uDCCD" + meeting.getLocation().getLocationLink().toString() + "\n"
                + meeting.getMeetingInfo().getSpeakingLevel().toString() + "\n"
                + meeting.getMeetingInfo().getTopic() + "\n"
                + meeting.getMeetingInfo().getQuestions().replace("●", "\n●") + "\n";
    }

    private SendMessage createSendMessage(RazykrashkaBot razykrashkaBot, String messageText) {
        return new SendMessage()
                .setChatId(razykrashkaBot.getUpdate().getMessage().getChat().getId())
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
}