package com.razykrashka.bot.stage.information.other;

import com.razykrashka.bot.constants.Telegraph;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Log4j2
@Component
public class VersionStage extends MainStage {

    @Value("${razykrashka.bot.version}")
    private String botVersion;

    public final static String KEYWORD = "/version";

    @Override
    public void handleRequest() {
        String message = "Razykrashka Bot\nVersion " + TextFormatter.getLink(botVersion, Telegraph.VERSION_HISTORY);
        messageManager
                .disableKeyboardLastBotMessage()
                .sendMessage(new SendMessage()
                        .enableHtml(true)
                        .disableWebPagePreview()
                        .setChatId(updateHelper.getChatId())
                        .setText(message));
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageTextEquals(KEYWORD)
                && !updateHelper.isMessageFromGroup();
    }
}