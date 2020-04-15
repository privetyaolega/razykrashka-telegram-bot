package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptLocationMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
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
        handleRequest(true);
    }

    public void handleRequest(boolean showTutorial) {
        meeting = getMeetingInCreation();
        meeting.setLocation(null);
        meetingRepository.save(meeting);

/*        if (!updateHelper.isCallBackDataEquals(this.getClass().getSimpleName() + EDIT) && showTutorial) {
            LoadingThreadV2 loadingThread = startLoadingThread(false);
            messageManager.sendAnimation("bot/pics/map_attachment.gif", getString("mapAttachment"));
            loadingThread.interrupt();
        }*/

        String meetingInfo = meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
        messageManager.deleteLastBotMessageIfHasKeyboard()
                .sendSimpleTextMessage(meetingInfo, getKeyboard());
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