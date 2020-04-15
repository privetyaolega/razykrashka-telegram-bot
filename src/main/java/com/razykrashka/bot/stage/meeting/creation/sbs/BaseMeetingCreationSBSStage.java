package com.razykrashka.bot.stage.meeting.creation.sbs;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.service.config.property.meeting.MeetingProperties;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.creation.IntroStartMeetingCreationStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.loading.LoadingThreadV2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseMeetingCreationSBSStage extends MainStage {

    @Autowired
    MeetingProperties meetingProperties;
    @Autowired
    CreationStateRepository creationStateRepository;
    @Autowired
    MeetingMessageUtils meetingMessageUtils;
    Meeting meeting;
    protected static final String EDIT = "edit";

    @Override
    public boolean isStageActive() {
        try {
            Optional<Meeting> meetingOptional = meetingRepository.findByCreationStatusEqualsInProgress(updateHelper.getUser().getId());
            if (meetingOptional.isPresent()) {
                CreationState creationState = meetingOptional.get().getCreationState();
                return creationState.getActiveStage().equals(this.getClass().getSimpleName())
                        && creationState.isInCreationProgress();
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    protected void setActiveNextStage(Class clazz) {
        meeting = getMeetingInCreation();
        CreationState creationState = meeting.getCreationState();
        creationState.setActiveStage(clazz.getSimpleName());
        creationStateRepository.save(creationState);

        meeting.setCreationState(creationState);
        meetingRepository.save(meeting);
    }

    protected Meeting getMeetingInCreation() {
        Optional<Meeting> meetingOptional = meetingRepository.findByCreationStatusEqualsInProgress(updateHelper.getUser().getId());

        if (!meetingOptional.isPresent()) {
            CreationState creationState = CreationState.builder()
                    .creationStatus(CreationStatus.IN_PROGRESS)
                    .inCreationProgress(true)
                    .startCreationDateTime(LocalDateTime.now())
                    .build();
            creationStateRepository.save(creationState);

            meeting = Meeting.builder()
                    .telegramUser(updateHelper.getUser())
                    .creationState(creationState)
                    .format(MeetingFormatEnum.NA)
                    .build();
            return meetingRepository.save(meeting);
        } else if (isSessionExpired(meetingOptional.get())) {

            Meeting expiredMeeting = meetingOptional.get();
            meetingRepository.delete(expiredMeeting);

            messageManager.disableKeyboardLastBotMessage()
                    .sendSimpleTextMessage("SESSION EXPIRED");

            razykrashkaBot.getContext().getBean(IntroStartMeetingCreationStage.class).handleRequest();
            throw new RuntimeException("SESSION EXPIRED");
        }
        return meetingOptional.get();
    }

    private boolean isSessionExpired(Meeting meeting) {
        int sessionTimeMinutes = meetingProperties.getSession();
        return meeting.getCreationState()
                .getStartCreationDateTime()
                .plusMinutes(sessionTimeMinutes).isBefore(LocalDateTime.now());
    }

    protected LoadingThreadV2 startLoadingThread(boolean fixIterationLoading) {
        LoadingThreadV2 thread = new LoadingThreadV2(fixIterationLoading);
        razykrashkaBot.getContext().getAutowireCapableBeanFactory().autowireBean(thread);
        thread.start();
        return thread;
    }

    protected void joinToThread(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}