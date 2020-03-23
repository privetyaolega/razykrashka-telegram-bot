package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import com.razykrashka.bot.ui.helpers.loading.LoadingThread;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptFinalMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Value("${razykrashka.group.id}")
    String groupChatId;
    @Value("${razykrashka.group.meeting.notification}")
    boolean meetingNotification;

    @Override
    public void handleRequest() {
        LoadingThread loadingThread = startLoadingThread();

        Meeting meeting = super.getMeetingInCreation();
        CreationState creationState = meeting.getCreationState();
        creationState.setCreationStatus(CreationStatus.DONE);
        creationState.setActiveStage(null);
        creationState.setInCreationProgress(false);
        creationStateRepository.save(creationState);

        meeting.setCreationDateTime(LocalDateTime.now());
        meeting.setCreationState(creationState);
        meeting.getParticipants().add(updateHelper.getUser());
        meetingRepository.save(meeting);
        try {
            loadingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String message = TextFormatter.getBoldString(String.format(getString("success"), meeting.getId()));
        messageManager.updateMessage(message)
                .sendSticker("success2.tgs");

        if (meetingNotification) {
            String meetingInfo = meetingMessageUtils.createMeetingInfoGroup(meeting);
            messageManager.sendMessage(new SendMessage()
                    .setParseMode(ParseMode.HTML)
                    .setChatId(groupChatId)
                    .setText(meetingInfo)
                    .disableWebPagePreview());
        }
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive() && updateHelper.isCallBackDataContains();
    }
}