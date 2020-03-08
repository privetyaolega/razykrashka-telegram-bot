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
                        "EN Instruction", this.getClass().getSimpleName() + "_en_instruction",
                        "RU Instruction", this.getClass().getSimpleName() + "_ru_instruction"))
                .setRow("Template", this.getClass().getSimpleName() + "_template")
                .setRow("Example", this.getClass().getSimpleName() + "_example")
                .setRow(new InlineKeyboardButton()
                        .setText("FAST MEETING CREATION DEBUG")
                        .setSwitchInlineQueryCurrentChat(this.getStringMap().get("example")))
                .build();
    }

    @Override
    public void handleRequest() {
        messageManager.sendSimpleTextMessage(getStringMap().get("enInstruction"), getKeyboard());
    }

    @Override
    public boolean processCallBackQuery() {
        String callBackData = updateHelper.getCallBackData();
        if (callBackData.equals(this.getClass().getSimpleName() + "_en_instruction") || razykrashkaBot.getRealUpdate().getCallbackQuery().getData().equals(this.getClass().getSimpleName())) {
            messageManager.updateMessage(getStringMap().get("enInstruction"), (InlineKeyboardMarkup) getKeyboard());
        }

        if (callBackData.equals(this.getClass().getSimpleName() + "_ru_instruction")) {
            messageManager.updateMessage(getStringMap().get("ruInstruction"), (InlineKeyboardMarkup) getKeyboard());
        }
        if (callBackData.equals(this.getClass().getSimpleName() + "_template")) {
            messageManager.updateMessage(getStringMap().get("template"), (InlineKeyboardMarkup) getKeyboard());
        }

        if (callBackData.equals(this.getClass().getSimpleName() + "_example")) {
            messageManager.updateMessage(getStringMap().get("example"), (InlineKeyboardMarkup) getKeyboard());
        }
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}