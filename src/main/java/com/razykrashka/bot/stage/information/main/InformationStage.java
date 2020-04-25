package com.razykrashka.bot.stage.information.main;

import com.razykrashka.bot.constants.Emoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class InformationStage extends InformationMainStage {

    public static final String KEYWORD = "Information";

    @Override
    public void processCallBackQuery() {
        messageManager.updateOrSendDependsOnLastMessageOwner("Have you been learning English for a long time but you are still afraid to speak it? " + Emoji.SCREAM +
                        "\nOr waiting for the ”right moment”? If you want to be fluent one day, you need to make practicing English your habit NOW! \n" +
                        "\n" +
                        "It’s amazing that you’re willing to improve!\n" +
                        "Well, guess what, there’s no such thing as perfect timing! If you want to see results tomorrow, you should start now.  \n" +
                        "\n" +
                        "Via this community you can find pen pals, speaking partners or even friends! We will be super happy to help you develop your speaking and listening skills.\n" +
                        "\n" +
                        "Join one of the available meetings or create a new one! You can be your own boss here \uD83D\uDE0E\n" +
                        "\n" +
                        "Let’s get started! /create",
                getKeyboardWithHighlightedButton("Main"));
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}