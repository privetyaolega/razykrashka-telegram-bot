package com.razykrashka.bot.service.config.job.task;

import com.google.common.collect.ImmutableSet;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.integration.discord.DiscordBot;
import com.razykrashka.bot.stage.meeting.view.single.info.SingleMeetingViewContactStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
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

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRightBeforeMeetingJob extends AbstractJob implements Runnable {

    @Autowired
    DiscordBot discordBot;
    @Autowired
    KeyboardBuilder keyboardBuilder;
    @Autowired
    ParticipantsMessageManager participantsMessageManager;

    String onlineMessage = "Hey! Your /meeting%s is about to start! " + Emoji.HUG + "\n\n" +
            "At %s we are waiting for you in this discord channel that was created specifically for your meeting " + Emoji.WINK + "\n" +
            "Try to be there little bit in advance, to avoid wasting time on organizational issues. " +
            "If you have any questions you can get in contact with meeting organizer\n\n" +
            "<b>Meeting time:</b> %s\n" +
            "<b>Channel name:</b> %s\n" +
            "<b>Discord channel:</b> %s";

    public void run() {
        log.info("JOB: Notification right before meeting job is started...");
        meetingService.getAllMeetingDateToday()
//        meetingService.getAllUpcomingMeetings()
//                .filter(m -> LocalDateTime.now().isAfter(m.getMeetingDateTime().minusHours(1)))
//                .filter(m -> LocalDateTime.now().plusHours(1).getHour() == m.getMeetingDateTime().getHour())
                .forEach(m1 -> {
                    if (m1.getFormat().equals(MeetingFormatEnum.ONLINE)) {
                        sendForOnlineMeetings(m1);
                    } else {
                        // TODO
                    }
                });
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
}