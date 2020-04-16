package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptPhoneMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.start.VerifyMeetingStateSBSStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    Class<? extends BaseMeetingCreationSBSStage> nextStageClass = AcceptPhoneMeetingCreationSBSStage.class;

    @Override
    public void processCallBackQuery() {
        meeting = getMeetingInCreation();
        if (updateHelper.getUser().getPhoneNumber().isEmpty()) {
            messageManager
                    .disableKeyboardLastBotMessage()
                    .sendMessage(new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setText(getString("shareNumber"))
                            .setReplyMarkup(getKeyboard())
                            .disableWebPagePreview()
                            .setChatId(updateHelper.getChatId()));

            setActiveNextStage(nextStageClass);
        } else {
            razykrashkaBot.getContext().getBean(VerifyMeetingStateSBSStage.class).processCallBackQuery();
        }
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton()
                .setText("Share My contact")
                .setRequestContact(true));

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}