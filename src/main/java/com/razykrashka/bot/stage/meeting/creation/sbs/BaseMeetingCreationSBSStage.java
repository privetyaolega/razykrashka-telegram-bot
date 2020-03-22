package com.razykrashka.bot.stage.meeting.creation.sbs;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.creation.SelectWayMeetingCreationStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.loading.LoadingThread;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Getter
@Setter
public abstract class BaseMeetingCreationSBSStage extends MainStage {

    @Value("${razykrashka.bot.meeting.session}")
    private long sessionTimeMinutes;
    @Autowired
    protected CreationStateRepository creationStateRepository;
    @Autowired
    protected MeetingMessageUtils meetingMessageUtils;
    protected Meeting meeting;

    @Override
    public boolean isStageActive() {
        Optional<Meeting> meetingOptional = meetingRepository.findByCreationStatusEqualsInProgress(updateHelper.getUser().getId());
        if (meetingOptional.isPresent()) {
            CreationState creationState = meetingOptional.get().getCreationState();
            return creationState.getActiveStage().equals(this.getClass().getSimpleName())
                    && creationState.isInCreationProgress();
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
                    .build();
            return meetingRepository.save(meeting);
        } else if (meetingOptional.get()
                .getCreationState()
                .getStartCreationDateTime()
                .plusMinutes(sessionTimeMinutes).isBefore(LocalDateTime.now())) {

            Meeting expiredMeeting = meetingOptional.get();
            meetingRepository.delete(expiredMeeting);

            messageManager.disableKeyboardLastBotMessage()
                    .sendSimpleTextMessage("SESSION EXPIRED");

            razykrashkaBot.getContext().getBean(SelectWayMeetingCreationStage.class).handleRequest();
            throw new RuntimeException("SESSION EXPIRED");
        }
        return meetingOptional.get();
    }

    protected LoadingThread startLoadingThread() {
        LoadingThread thread = new LoadingThread();
        razykrashkaBot.getContext().getAutowireCapableBeanFactory().autowireBean(thread);
        thread.start();
        return thread;
    }
}