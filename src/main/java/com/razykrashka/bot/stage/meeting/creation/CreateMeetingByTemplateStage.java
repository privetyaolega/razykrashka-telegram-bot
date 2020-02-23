package com.razykrashka.bot.stage.meeting.creation;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Log4j2
@Component
public class CreateMeetingByTemplateStage extends MainStage {

    public CreateMeetingByTemplateStage() {
        stageInfo = StageInfo.CREATE_MEETING_BY_TEMPLATE_STAGE;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        "EN Instruction", stageInfo.getStageName() + "_en_instruction",
                        "RU Instruction", stageInfo.getStageName() + "_ru_instruction"))
                .setRow("Template", stageInfo.getStageName() + "_template")
                .setRow("Example", stageInfo.getStageName() + "_example")
                .setRow(new InlineKeyboardButton()
                        .setText("FAST MEETING CREATION DEBUG")
                        .setSwitchInlineQueryCurrentChat(this.getStringMap().get("example")))
                .build();
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