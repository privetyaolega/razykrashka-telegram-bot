package com.razykrashka.bot.stage;

import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

@Log4j2
@Component
@Builder
public class UndefinedStage extends MainStage {

    public UndefinedStage() {
        stageInfo = StageInfo.UNDEFINED;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return null;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }

    @Override
    public boolean isStageActive() {
        return false;
    }

    @Override
    public void handleRequest() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("html");
        sendMessage.setChatId(update.getMessage().getChat().getId());
        sendMessage.setText(String.format(getStringMap().get("unknown"), update.getMessage().getText()));
        sendMessage.setReplyMarkup(null);

        razykrashkaBot.executeBot(sendMessage);
    }
}