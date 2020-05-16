package com.razykrashka.bot.stage.information;

import com.razykrashka.bot.stage.MainStage;
import org.springframework.stereotype.Component;

@Component
public class UndefinedStage extends MainStage {

    @Override
    public boolean isStageActive() {
        return false;
    }

    @Override
    public void handleRequest() {
        if (updateHelper.hasMessage()
                && updateHelper.getUpdate().getMessage().hasText()
                && !updateHelper.isMessageFromGroup()) {
            messageManager
                    .disableKeyboardLastBotMessage()
                    .replyLastMessage(getString("unknown"));
        }
    }
}