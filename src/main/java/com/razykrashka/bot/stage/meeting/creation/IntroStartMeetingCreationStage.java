package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.constants.Emoji;
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
        return keyboardBuilder.getKeyboard()
                .setRow("Let's start! " + Emoji.ROCK_HAND, PhoneMeetingCreationSBSStage.class.getSimpleName())
                .build();
    }

    @Override
    public void handleRequest() {
        messageManager
                .disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(getString("main"), this.getKeyboard());
              /*  .sendSimpleTextMessage("We are happy, that you understand that practice makes perfect!" + Emoji.BICEPS + "\n\n" +
                        "Actually, meeting creation is peace of cake and it won't take much of your time \n" +
                        "All detailed information related to meeting you can find " + TextFormatter.getLink("here", "http://google.com") + "\n\n" +
                        "Right now, you are about to answering some common questions about meeting details.\n" +
                        "Just follow tips that will be written in the bottom of each message.\n" +
                        "\n" +
                        "After creation the meeting show up in <code>\"Meeting\"</code> sections and everyone of our community will know about it by getting notification in our community group.\n" +
                        "\n" +
                        "Sooo, are you readyyyyyy?", this.getKeyboard());*/
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isMessageTextEquals(KEYWORD)
                || updateHelper.isMessageTextEquals("/create"))
                && !updateHelper.isMessageFromGroup();
    }
}