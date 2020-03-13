package com.razykrashka.bot.stage.meeting.creation.sbs;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.Stage;
import com.razykrashka.bot.stage.meeting.creation.SelectWayMeetingCreationStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.loading.LoadingThread;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Getter
@Setter
public abstract class BaseMeetingCreationSBSStage extends MainStage {

    @Autowired
    protected MeetingMessageUtils meetingMessageUtils;
    protected Meeting meeting;
    private static final long SESSION_TIME_MINUTES = 2;

    @Override
    public boolean isStageActive() {
        return super.getStageActivity();
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    protected void setActiveNextStage(Class clazz) {
        razykrashkaBot.getStages().forEach(stage -> stage.setActive(false));
        Stage stage = ((Stage) razykrashkaBot.getContext().getBean(clazz));
        stage.setActive(true);
    }

    protected Meeting getMeetingInCreation() {
        Optional<Meeting> meetingOptional = meetingRepository.findTop1ByCreationStatusEqualsAndTelegramUser(
                CreationStatus.IN_PROGRESS, razykrashkaBot.getUser());

        if (!meetingOptional.isPresent()) {
            Meeting meeting = new Meeting();
            meeting.setCreationStatus(CreationStatus.IN_PROGRESS);
            meeting.setTelegramUser(razykrashkaBot.getUser());
            meeting.setCreationDateTime(LocalDateTime.now());
            meeting.setStartCreationDateTime(LocalDateTime.now());
            return meeting;
        } else if (meetingOptional.get()
                .getStartCreationDateTime()
                .plusMinutes(SESSION_TIME_MINUTES).isBefore(LocalDateTime.now())) {

            Meeting expiredMeeting = meetingOptional.get();
            meetingRepository.delete(expiredMeeting);

            messageManager.disableKeyboardLastBotMessage();
            messageManager.sendSimpleTextMessage("SESSION EXPIRED");

            razykrashkaBot.getStages().forEach(stage -> stage.setActive(false));
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