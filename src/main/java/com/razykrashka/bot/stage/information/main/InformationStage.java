package com.razykrashka.bot.stage.information.main;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class InformationStage extends InformationMainStage {

    public static final String KEYWORD = "Information";

    @Override
    public void processCallBackQuery() {
        messageManager.updateOrSendDependsOnLastMessageOwner("\uD83C\uDF08 Welcome to community whose main goal is cohesion of people learning English; improvement, development and comprehensive support of all skills related to language. \uD83C\uDF08\n" +
                        "You can create some meeting to speak or join to existing one. \uD83D\uDE4F\uD83C\uDFFB" +
                        "\n\nControl commands manual you can find here /help",
                getKeyboardWithHighlightedButton("Main"));
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}