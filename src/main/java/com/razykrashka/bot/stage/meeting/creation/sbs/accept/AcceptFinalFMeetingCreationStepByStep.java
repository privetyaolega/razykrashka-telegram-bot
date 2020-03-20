package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.loading.LoadingThread;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;

@Log4j2
@Component
@PropertySource(value = "classpath:/props/razykrashka.yaml", factory = YamlPropertyLoaderFactory.class)
public class AcceptFinalFMeetingCreationStepByStep extends BaseMeetingCreationSBSStage {

    @Value("${razykrashka.group.id}")
    private String groupChatId;

    @Override
    public void handleRequest() {
        LoadingThread loadingThread = startLoadingThread();

        Meeting meeting = super.getMeetingInCreation();
        meeting.setCreationDateTime(LocalDateTime.now());
        meeting.setCreationStatus(CreationStatus.DONE);
        meeting.getParticipants().add(updateHelper.getUser());
        meetingRepository.save(meeting);
        try {
            loadingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messageManager.updateMessage("MEETING CREATED")
                .sendSticker("success2.tgs");

        String meetingInfo = meetingMessageUtils.createGroupMeetingInfo(meeting);
        messageManager.sendMessage(new SendMessage()
                .setParseMode(ParseMode.HTML)
                .setChatId(groupChatId)
                .setText(meetingInfo)
                .disableWebPagePreview());
    }

    @Override
    public boolean isStageActive() {
        return super.getStageActivity() && updateHelper.isCallBackDataContains();
    }
}