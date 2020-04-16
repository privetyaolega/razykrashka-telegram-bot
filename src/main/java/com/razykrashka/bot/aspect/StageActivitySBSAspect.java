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
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StageActivitySBSAspect {

    @Autowired
    UpdateHelper updateHelper;
    @Autowired
    CreationStateRepository creationStateRepository;
    @Autowired
    MeetingRepository meetingRepository;

    List<String> keyWordsList = Arrays.asList("Create Meeting", "View Meetings", "My Meetings", "Information");


    @Pointcut("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))))")
    public void updateReceivedPointcut() {
    }

    @Around("updateReceivedPointcut()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Integer id = updateHelper.getUser().getId();
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

        Object proceed = joinPoint.proceed(joinPoint.getArgs());

        BaseMeetingCreationSBSStage.setActiveStage("");
        return proceed;
    }

    private boolean isMainStage() {
        return keyWordsList.contains(updateHelper.getMessageText());
    }
}