package com.razykrashka.bot.stage.information.main;

import com.razykrashka.bot.constants.Telegraph;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SupportUsStage extends InformationMainStage {

    public SupportUsStage() {
        buttonLabel = "Support Us";
    }

    @Override
    public void processCallBackQuery() {
        String message = getFormatString("main", Telegraph.SUPPORT_US);
        messageManager.updateOrSendDependsOnLastMessageOwner(message, getKeyboardWithHighlightedButton());
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}