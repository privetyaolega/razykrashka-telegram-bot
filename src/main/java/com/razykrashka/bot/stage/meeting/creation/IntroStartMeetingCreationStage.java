package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.constants.Telegraph;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.PhoneMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class IntroStartMeetingCreationStage extends MainStage {

    public static final String KEYWORD = "Create Meeting";

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder
                .getKeyboard()
                .setRow("Let's start! " + Emoji.ROCK_HAND, PhoneMeetingCreationSBSStage.class.getSimpleName())
                .build();
    }

    @Override
    public void handleRequest() {
        messageManager
                .disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(getFormatString("main", Telegraph.EN_FAQ), this.getKeyboard());
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isMessageTextEquals(KEYWORD)
                || updateHelper.isMessageTextEquals("/create"))
                && !updateHelper.isMessageFromGroup();
    }
}