package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.job.AbstractJob;
import com.razykrashka.bot.service.config.job.properties.JobRunnable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.support.CronTrigger;

@Getter
@Setter
@ToString
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableMeetingsNotificationJob extends AbstractJob implements JobRunnable {

    boolean enabled;
    String cronExp;
    String name = "Available Job";

    final static String MAIN_MESSAGE = "Hey, guys! " + Emoji.WAVE_HAND + "\n" +
            "Yahooooo! There are %s available meetings!\n" +
            "Hurry up and join! Practice makes perfect. " + Emoji.BICEPS;
    final static String NO_MEETINGS_MESSAGE = "There is no any available meeting. Work hard and create meeting!";

    /**
     *
     * Job notifies about available(vacant) meetings in main group chat.
     * Message consist of:
     * - message with amount of meetings and some common information
     * - inline button that is trigger to AllMeetingViewStage for calling user.
     * The message is sent to calling user directly (user chat);
     *
     * Period: every day;
     *
     */

    @Override
    public void run() {
        log.info("TEST MESSAGE FROM AVAILABLE JOB");
/*        log.info("JOB: Available meeting notification job is started.");
        List<Meeting> availableMeetings = meetingService.getAllCreationStatusDone();
        String message;
        InlineKeyboardMarkup keyboard = null;
        if (availableMeetings.isEmpty()) {
            message = NO_MEETINGS_MESSAGE;
        } else {
            message = String.format(MAIN_MESSAGE, availableMeetings.size());
            keyboard = keyboardBuilder.getKeyboard()
                    .setRow("Show available meetings ✨",
                            OfflineMeetingsViewStage.class.getSimpleName() + FROM_GROUP)
                    .build();
        }
        messageManager.disableKeyboardLastBotMessage(groupChatId)
                .sendMessage(new SendMessage()
                        .setParseMode(ParseMode.HTML)
                        .setChatId(groupChatId)
                        .setText(message)
                        .setReplyMarkup(keyboard));*/
    }

    @Override
    public CronTrigger getCronTrigger() {
        return new CronTrigger(cronExp);
    }
}
