package com.razykrashka.bot.stage.information;

import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@Builder
public class UndefinedStage extends MainStage {

    public UndefinedStage() {
        stageInfo = StageInfo.UNDEFINED;
    }

    @Override
    public boolean isStageActive() {
        return false;
    }

    @Override
    public void handleRequest() {
        if (updateHelper.hasMessage()
                && updateHelper.getUpdate().getMessage().hasText()
                && !updateHelper.isUpdateFromGroupChat()) {
            String message = getFormatString("unknown", updateHelper.getMessageText());
            messageManager.disableKeyboardLastBotMessage()
                    .replyLastMessage(message);
        }
    }
}