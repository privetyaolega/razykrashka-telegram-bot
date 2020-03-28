package com.razykrashka.bot.job;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.db.entity.razykrashka.poll.TelegramPoll;
import com.razykrashka.bot.db.entity.razykrashka.poll.TelegramPollOption;
import com.razykrashka.bot.db.repo.*;
import com.razykrashka.bot.exception.YandexMapApiException;
import com.razykrashka.bot.stage.meeting.view.all.OfflineMeetingsViewStage;
import com.razykrashka.bot.ui.helpers.LocationHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AutoCreationMeetingBasedOnPollJob extends AbstractJob {

    @Value("${razykrashka.job.meeting.auto-creation.enabled}")
    boolean jobEnabled;

    @Autowired
    LocationHelper locationHelper;
    @Autowired
    PollRepository pollRepository;
    @Autowired
    protected MeetingInfoRepository meetingInfoRepository;
    @Autowired
    protected LocationRepository locationRepository;
    @Autowired
    protected TelegramMessageRepository telegramMessageRepository;
    @Autowired
    protected CreationStateRepository creationStateRepository;
    @Autowired
    protected MeetingRepository meetingRepository;

    /**
     *
     * Job creates poll in group chat
     * Poll contains question, that would be foundation
     * for subsequent meeting creation.
     *
     * Period: conditional (preliminary: two days without
     * meeting activities)
     *
     */
    @Bean
    @Scheduled(fixedRateString = "${razykrashka.job.meeting.auto-creation.cron}")
    public void pollCreationJob() {
        if (jobEnabled) {
            String question = "Auto meeting creation.\nWhen would you like to create auto meeting?";
            List<String> answerOptions = Arrays.asList(
                    "27.03.2020",
                    "28.03.2020",
                    "29.03.2020");
            messageManager.disableKeyboardLastBotMessage(groupChatId)
                    .sendPoll(groupChatId, question, answerOptions);
        }
    }

    /**
     *
     * Job processes poll results, that was created by
     * previous job
     * Based on these results, job creates meeting and send
     * notification message in group chat
     *
     * Period: in some time, after first job (in order to
     * more poll results)
     */
    @Scheduled(fixedRateString = "${razykrashka.job.meeting.auto-creation.cron}")
    public void meetingCreationBasedOnPollResultsJob() throws InterruptedException, YandexMapApiException {
        if (jobEnabled) {

            Thread.sleep(10_000);

            TelegramPoll poll = pollRepository.getLastCreatedPoll().get();
            TelegramPollOption topResult = poll.getTelegramPollOptions().stream()
                    .max(Comparator.comparing(TelegramPollOption::getCount)).get();
            LocalDate meetingDate = LocalDate.parse(topResult.getTextOption(), DateTimeFormatter.ofPattern("d.MM.yyyy"));

            List<MeetingInfo> meetingInfoList = meetingInfoRepository.findAllByParticipantLimitEquals(0);
            MeetingInfo meetingInfo = meetingInfoList.get(new Random().nextInt(meetingInfoList.size()));
            meetingInfoRepository.save(meetingInfo);

            Location location = locationHelper.getLocation("Кальварийская 46");
            locationRepository.save(location);

            CreationState creationState = CreationState.builder()
                    .creationStatus(CreationStatus.DONE)
                    .build();
            creationStateRepository.save(creationState);

            Meeting meeting = Meeting.builder()
                    .meetingDateTime(meetingDate.atTime(0, 0, 0))
                    .creationDateTime(LocalDateTime.now())
                    .meetingInfo(meetingInfo)
                    .location(location)
                    .creationState(creationState)
                    .participants(new HashSet<>())
                    .build();
            meetingRepository.save(meeting);

            InlineKeyboardMarkup keyboard = keyboardBuilder.getKeyboard()
                    .setRow("Show available meetings ✨",
                            OfflineMeetingsViewStage.class.getSimpleName() + "fromGroup")
                    .build();

            messageManager.disableKeyboardLastBotMessage(groupChatId)
                    .sendMessage(new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(groupChatId)
                            .setText("Meeting created! Date " + meetingDate.toString() + ". Meeting ID: " + meeting.getId())
                            .setReplyMarkup(keyboard));
        }
    }
}