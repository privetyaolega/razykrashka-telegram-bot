package com.razykrashka.bot.stage.meeting.creation.sbs;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class IncorrectInputFormatSBSStage extends BaseMeetingCreationSBSStage {
    @Override
    public void handleRequest() {
        messageManager.disableKeyboardLastBotMessage()
                .replyLastMessage(getString("main"));
    }
}