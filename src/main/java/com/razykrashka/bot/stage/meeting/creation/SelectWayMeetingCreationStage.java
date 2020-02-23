package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@Component
public class SelectWayMeetingCreationStage extends MainStage {

    public SelectWayMeetingCreationStage() {
        stageInfo = StageInfo.SELECT_WAY_MEETING_CREATION;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList();

        keyboardButtonsRow1.add(new InlineKeyboardButton()
                .setText(StageInfo.CREATE_MEETING_BY_TEMPLATE_STAGE.getKeyword())
                .setCallbackData(this.getStageInfo().getStageName()));
        keyboardButtonsRow1.add(new InlineKeyboardButton()
                .setText(StageInfo.CREATE_MEETING_ON_STEPS.getKeyword())
                .setCallbackData(this.getClass().getSimpleName() + "_test2"));

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(keyboardButtonsRow1));
        return inlineKeyboardMarkup;
    }

    @Override
    public void handleRequest() {
        messageSender.sendSimpleTextMessage(this.getStringMap().get("enMain"), this.getKeyboard());
    }

    @Override
    public boolean processCallBackQuery() {
        messageSender.updateMessage("YOOOHOO, Let's create new meeting together! \uD83D\uDE0D");
        razykrashkaBot.getContext().getBean(CreateMeetingByTemplateStage.class).handleRequest();
        return true;
    }
}