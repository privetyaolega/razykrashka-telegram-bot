package com.razykrashka.bot.aspect;


import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.db.repo.MeetingRepository;
import com.razykrashka.bot.stage.information.WelcomeStage;
import com.razykrashka.bot.stage.information.main.HelpStage;
import com.razykrashka.bot.stage.information.main.InformationStage;
import com.razykrashka.bot.stage.information.other.CurrentTimeStage;
import com.razykrashka.bot.stage.information.other.VersionStage;
import com.razykrashka.bot.stage.meeting.creation.IntroStartMeetingCreationStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.all.*;
import com.razykrashka.bot.stage.meeting.view.single.SingleMeetingViewBaseStage;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StageActivitySBSAspect {

    final CreationStateRepository creationStateRepository;
    final MeetingRepository meetingRepository;
    final UpdateHelper updateHelper;
    final List<String> keyWordsList;

    public StageActivitySBSAspect(UpdateHelper updateHelper,
                                  CreationStateRepository creationStateRepository,
                                  MeetingRepository meetingRepository) {
        this.updateHelper = updateHelper;
        this.creationStateRepository = creationStateRepository;
        this.meetingRepository = meetingRepository;
        this.keyWordsList = new ArrayList<>(Arrays.asList(
                WelcomeStage.KEYWORD,
                HelpStage.KEYWORD,
                SelectMeetingsTypeStage.KEYWORD,
                SingleMeetingViewBaseStage.KEYWORD,
                ArchivedMeetingsViewStage.KEYWORD,
                OfflineMeetingsViewStage.KEYWORD,
                OnlineMeetingsViewStage.KEYWORD,
                VersionStage.KEYWORD,
                CurrentTimeStage.KEYWORD
        ));
        keyWordsList.addAll(MyMeetingsViewStage.KEYWORDS);
        keyWordsList.addAll(ActiveMeetingsViewStage.KEYWORDS);
        keyWordsList.addAll(InformationStage.KEYWORDS);
        keyWordsList.addAll(IntroStartMeetingCreationStage.KEYWORDS);
    }

    @Pointcut("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))))")
    public void updateReceivedPointcut() {
    }

    @Before("updateReceivedPointcut()")
    public void measureExecutionTime() {
        Integer id = updateHelper.getTelegramUserId();
        Optional<Meeting> meetingOptional = meetingRepository.findByCreationStatusEqualsInProgress(id);

        if (isMainStage()) {
            if (meetingOptional.isPresent()) {
                Meeting meeting = meetingOptional.get();
                CreationState creationState = meeting.getCreationState();
                creationState.setInCreationProgress(false);
                creationStateRepository.save(creationState);

                meeting.setCreationState(creationState);
                meetingRepository.save(meeting);
            }
        }

        if (meetingOptional.isPresent()) {
            Meeting meeting = meetingOptional.get();
            if (meeting.getCreationState().isInCreationProgress()) {
                String activeStage = meeting.getCreationState().getActiveStage();
                BaseMeetingCreationSBSStage.setActiveStage(activeStage);
            }
        }
    }

    @SneakyThrows
    @Before("updateReceivedPointcut()")
    public void deleteGroupMessages() {
        Update update = updateHelper.getUpdate();
        if (updateHelper.isMessageFromGroup()
                && (messageStartsWithKeyword()
                || update.getMessage().getLeftChatMember() != null
                || updateHelper.isNewChatMember())) {
            updateHelper.getBot().execute(new DeleteMessage()
                    .setChatId(updateHelper.getChatId())
                    .setMessageId(updateHelper.getUpdate().getMessage().getMessageId()));
        }
    }

    public boolean messageStartsWithKeyword() {
        if (updateHelper.hasMessage() && updateHelper.getUpdate().getMessage().hasText()) {
            return keyWordsList.stream().anyMatch(k -> updateHelper.getMessageText().startsWith(k));
        }
        return false;
    }

    @After("updateReceivedPointcut()")
    public void measureExecutionTimeAfter() {
        BaseMeetingCreationSBSStage.setActiveStage("");
    }

    private boolean isMainStage() {
        return keyWordsList.contains(updateHelper.getMessageText())
                || updateHelper.isMessageContains("/meeting");
    }
}