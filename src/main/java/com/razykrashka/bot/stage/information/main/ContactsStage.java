package com.razykrashka.bot.stage.information.main;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ContactsStage extends InformationMainStage {

    public ContactsStage() {
        buttonLabel = "Contacts";
    }

    @Override
    public void processCallBackQuery() {
        messageManager.updateMessage(getString("main"), getKeyboardWithHighlightedButton());
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}