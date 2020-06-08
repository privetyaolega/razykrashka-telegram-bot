package com.razykrashka.bot.stage.information.other;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Log4j2
@Component
public class CurrentTimeStage extends MainStage {

    public final static String KEYWORD = "/time";

    @Override
    public void handleRequest() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH));
        messageManager
                .disableKeyboardLastBotMessage()
                .sendMessage(new SendMessage()
                        .enableHtml(true)
                        .disableWebPagePreview()
                        .setChatId(updateHelper.getChatId())
                        .setText(TextFormatter.getBoldString("Moscow time:\n") +
                                TextFormatter.getItalicString(currentTime) + " " + Emoji.CLOCK));
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageTextEquals(KEYWORD)
                && !updateHelper.isMessageFromGroup();
    }
}