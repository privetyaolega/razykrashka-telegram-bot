package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.constants.Telegraph;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.PhoneMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class IntroStartMeetingCreationStage extends MainStage {

    public static final List<String> KEYWORDS = Arrays.asList(Emoji.BOOM + " Create a Meeting " + Emoji.BOOM, "/create");

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
        return KEYWORDS.contains(updateHelper.getMessageText())
                && !updateHelper.isMessageFromGroup();
    }
}