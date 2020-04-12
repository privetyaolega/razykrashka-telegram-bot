package com.razykrashka.bot.stage.meeting.edit.delete;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.view.single.SingleMeetingViewMainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class DeleteConfirmationSingleMeetingStage extends MainStage {

    @Override
    public boolean processCallBackQuery() {
        Integer id = updateHelper.getIntegerPureCallBackData();
        keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        "Yes", DeleteSingleMeetingStage.class.getSimpleName() + id,
                        "No", SingleMeetingViewMainStage.class.getSimpleName() + id))
                .build();

        messageManager.updateMessage(getFormatString("warning", id), keyboardBuilder.build());
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}