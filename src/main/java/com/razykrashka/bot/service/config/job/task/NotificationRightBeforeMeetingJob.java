package com.razykrashka.bot.service.config.job.task;

import com.google.common.collect.ImmutableSet;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.integration.discord.DiscordBot;
import com.razykrashka.bot.stage.meeting.view.single.info.SingleMeetingViewContactStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import com.razykrashka.bot.ui.helpers.LocationHelper;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import com.razykrashka.bot.ui.helpers.sender.ParticipantsMessageManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRightBeforeMeetingJob extends AbstractJob implements Runnable {

    @Autowired
    LocationHelper locationHelper;
    @Autowired
    DiscordBot discordBot;
    @Autowired
    KeyboardBuilder keyboardBuilder;
    @Autowired
    ParticipantsMessageManager participantsMessageManager;

    String onlineMessage = "Hey! Your /meeting%s is about to begin! " + Emoji.HUG + "\n\n" +
            "At %s we will be waiting for you in this discord channel. It was created just for your meeting " + Emoji.WINK + "\n" +
            "Try to be there in advance, to avoid wasting any time on organizational issues. " +
            "If you have any questions you can get in touch with the meeting organizer.\nGood luck! " + Emoji.SHAMROCK + "\n\n" +
            "\t<b>Meeting time:</b> %s\n" +
            "\t<b>Channel name:</b> %s\n" +
            "\t<b>Discord channel:</b> %s";

    String offlineMessage = "Hey there! Your /meeting%s is about to begin! " + Emoji.HUG + "\n\n" +
            "At %s we will be waiting for you at this address:\n%s\n\n" +
            "Try to be there in advance, to avoid wasting any time on organizational issues. " +
            "If you have any questions you can get in touch with the meeting organizer" + Emoji.WINK + "\n\n" +
            "Good luck! " + Emoji.SHAMROCK + "\n\n" +
            "\t<b>Meeting time:</b> %s\n" +
            "\t<b>Address:</b> %s";

    public void run() {
        log.info("JOB: Notification right before meeting job is started...");
        List<Meeting> meetings = meetingService.getAllActive()
                .filter(m -> LocalDateTime.now().plusHours(1).getHour() == m.getMeetingDateTime().getHour())
                .collect(Collectors.toList());
        meetings.forEach(m1 -> {
            if (m1.getFormat().equals(MeetingFormatEnum.ONLINE)) {
                sendForOnlineMeetings(m1);
            } else {
                sendForOfflineMeetings(m1);
            }
        });
        if (meetings.isEmpty()) {
            log.info("JOB: No meetings for {} hours", LocalDateTime.now().plusHours(1).getHour());
        }
    }

    private void sendForOnlineMeetings(Meeting m) {
        String channelName = "meeting # " + m.getId() + " - " + m.getMeetingInfo().getTopic();
        String discordInviteLink = discordBot.createVoiceMeetingChannel(m);
        String time = m.getMeetingDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String message = String.format(onlineMessage, m.getId(),
                TextFormatter.getBoldString(time),
                TextFormatter.getItalicString(time),
                TextFormatter.getItalicString(channelName),
                discordInviteLink);
        SendMessage sendMessage = new SendMessage()
                .setParseMode(ParseMode.HTML)
                .setText(message)
                .setReplyMarkup(keyboardBuilder
                        .getKeyboard()
                        .setRow(ImmutableSet.of(
                                new InlineKeyboardButton("Contact " + Emoji.ONE_PERSON_SILHOUETTE)
                                        .setCallbackData(SingleMeetingViewContactStage.class.getSimpleName() + m.getId()),
                                new InlineKeyboardButton("Discord channel " + Emoji.INTERNET)
                                        .setUrl(discordInviteLink)))
                        .build());
        participantsMessageManager.sendMessageToAllParticipants(m, sendMessage);
        log.info("NOTIFICATION RIGHT BEFORE: invite link was sent to participants for online meeting {}", m.getId());
    }

    private void sendForOfflineMeetings(Meeting m) {
        String time = m.getMeetingDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String message = String.format(offlineMessage, m.getId(),
                TextFormatter.getBoldString(time),
                TextFormatter.getItalicString(locationHelper.getLocationLink(m)),
                TextFormatter.getItalicString(time),
                TextFormatter.getItalicString(locationHelper.getLocationLink(m)));
        SendMessage sendMessage = new SendMessage()
                .setParseMode(ParseMode.HTML)
                .setText(message)
                .setReplyMarkup(keyboardBuilder
                        .getKeyboard()
                        .setRow(ImmutableSet.of(
                                new InlineKeyboardButton("Contact " + Emoji.ONE_PERSON_SILHOUETTE)
                                        .setCallbackData(SingleMeetingViewContactStage.class.getSimpleName() + m.getId()),
                                new InlineKeyboardButton("Map " + Emoji.COFFEE)
                                        .setUrl(locationHelper.getLocationUrl(m))))
                        .build());
        participantsMessageManager.sendMessageToAllParticipants(m, sendMessage);
        log.info("NOTIFICATION RIGHT BEFORE: invite link was sent to participants for offline meeting {}", m.getId());
    }
}