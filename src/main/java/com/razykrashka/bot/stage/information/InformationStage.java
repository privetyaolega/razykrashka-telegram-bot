package com.razykrashka.bot.stage.information;

import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.information.stats.MainStatisticStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class InformationStage extends MainStage {

    public static final String KEYWORD = "Information";

    @Override
    public void handleRequest() {
        messageManager.updateOrSendDependsOnLastMessageOwner("\uD83C\uDF08 Welcome to community whose main goal is cohesion of people learning English; improvement, development and comprehensive support of all skills related to language. \uD83C\uDF08\n" +
                "You can create some meeting to speak or join to existing one. \uD83D\uDE4F\uD83C\uDFFB" +
                "\n\nControl commands manual you can find here /help", this.getKeyboard());
    }

    @Override
    public void processCallBackQuery() {
        handleRequest();
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow("Statistics", MainStatisticStage.class.getSimpleName())
                .build();
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}