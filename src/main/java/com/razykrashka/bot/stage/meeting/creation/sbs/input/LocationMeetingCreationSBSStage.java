package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptLocationMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.loading.LoadingThreadV2;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class LocationMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    Class<? extends BaseMeetingCreationSBSStage> nextStageClass = AcceptLocationMeetingCreationSBSStage.class;
    Class<? extends BaseMeetingCreationSBSStage> previousStageClass = FormatMeetingCreationSBSStage.class;

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        meeting.setLocation(null);
        meetingRepository.save(meeting);

        if (!updateHelper.isCallBackDataEquals(this.getClass().getSimpleName() + EDIT)) {
            LoadingThreadV2 loadingThread = startLoadingThread(false);
            messageManager.sendAnimation("bot/pics/map_attachment.gif", getString("mapAttachment"));
            loadingThread.interrupt();
            messageManager.deleteLastBotMessage();
        }

        String meetingInfo = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.deleteLastBotMessageIfHasKeyboard()
                .sendSimpleTextMessage(meetingInfo + getString("input"), getKeyboard());
        super.setActiveNextStage(nextStageClass);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(getString("backButton"), previousStageClass.getSimpleName() + EDIT)
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataEquals(this.getClass().getSimpleName() + EDIT);
    }
}