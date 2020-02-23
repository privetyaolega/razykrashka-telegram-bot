package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class CreateMeetingByTemplateStage extends MainStage {

    public CreateMeetingByTemplateStage() {
        stageInfo = StageInfo.CREATE_MEETING_BY_TEMPLATE_STAGE;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("EN Instruction").setCallbackData(stageInfo.getStageName() + "_en_instruction"));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("RU Instruction").setCallbackData(stageInfo.getStageName() + "_ru_instruction"));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Template").setCallbackData(stageInfo.getStageName() + "_template"));
        keyboardButtonsRow3.add(new InlineKeyboardButton().setText("Example").setCallbackData(stageInfo.getStageName() + "_example"));

        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList();
        keyboardButtonsRow4.add(new InlineKeyboardButton().setText("FAST MEETING CREATION DEBUG")
                .setSwitchInlineQueryCurrentChat(this.getStringMap().get("example")));

        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow1, keyboardButtonsRow2, keyboardButtonsRow3, keyboardButtonsRow4));
        return inlineKeyboardMarkup;
    }

    @Override
    public void handleRequest() {
        messageSender.sendSimpleTextMessage(getStringMap().get("enInstruction"), getKeyboard());
    }

    @Override
    public boolean processCallBackQuery() {
        String callBackData = razykrashkaBot.getCallbackQuery().getData();
        if (callBackData.equals(stageInfo.getStageName() + "_en_instruction")) {
            messageSender.updateMessage(getStringMap().get("enInstruction"), (InlineKeyboardMarkup) getKeyboard());
        }

        if (callBackData.equals(stageInfo.getStageName() + "_ru_instruction")) {
            messageSender.updateMessage(getStringMap().get("ruInstruction"), (InlineKeyboardMarkup) getKeyboard());
        }
        if (callBackData.equals(stageInfo.getStageName() + "_template")) {
            messageSender.updateMessage(getStringMap().get("template"), (InlineKeyboardMarkup) getKeyboard());
        }

        if (callBackData.equals(stageInfo.getStageName() + "_example")) {
            messageSender.updateMessage(getStringMap().get("example"), (InlineKeyboardMarkup) getKeyboard());
        }
        return true;
    }
}