package com.razykrashka.bot.aspect;


import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.db.repo.MeetingRepository;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

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

    public StageActivitySBSAspect(UpdateHelper updateHelper, CreationStateRepository creationStateRepository,
                                  MeetingRepository meetingRepository) {
        this.updateHelper = updateHelper;
        this.creationStateRepository = creationStateRepository;
        this.meetingRepository = meetingRepository;
        this.keyWordsList = Arrays.asList("Create Meeting",
                "View Meetings",
                "My Meetings",
                "Information");
    }


    @Pointcut("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))))")
    public void updateReceivedPointcut() {
    }

    @Before("updateReceivedPointcut()")
    public void measureExecutionTime() {
        Integer id = updateHelper.getTelegramUserId();
        Optional<Meeting> meetingOptional = meetingRepository.findByCreationStatusEqualsInProgress(id);

        if (isMainStage() || updateHelper.isUpdateFromGroup()) {
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

    private boolean isMainStage() {
        return keyWordsList.contains(updateHelper.getMessageText());
    }
}