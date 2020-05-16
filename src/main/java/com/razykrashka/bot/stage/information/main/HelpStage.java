package com.razykrashka.bot.stage.information.main;

import com.razykrashka.bot.constants.Telegraph;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
@PropertySource(value = "classpath:/props/razykrashka.yaml", factory = YamlPropertyLoaderFactory.class)
public class HelpStage extends InformationMainStage {

    @Value("${razykrashka.bot.version}")
    private String botVersion;
    public final static String KEYWORD = "/help";

    public HelpStage() {
        buttonLabel = "Help";
    }

    @Override
    public void handleRequest() {
        String message = getFormatString("main", TextFormatter.getLink("\nversion " + botVersion, Telegraph.CHANGELOG));
        ReplyKeyboard keyboard = getKeyboardWithHighlightedButton();
        messageManager.updateOrSendDependsOnLastMessageOwner(message, keyboard);
    }

    @Override
    public void processCallBackQuery() {
        handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}