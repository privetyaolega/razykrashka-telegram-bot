package com.razykrashka.bot.stage;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class IntroCreateMeetingStage extends MainStage {

    public IntroCreateMeetingStage() {
        stageInfo = StageInfo.INTRO_CREATE_MEETING;
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
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(razykrashkaBot.getUpdate().getMessage().getChat().getId());
        sendMessage.setText(getStringMap().get("enInstruction"));
        sendMessage.setReplyMarkup(getKeyboard());

        razykrashkaBot.executeBot(sendMessage);

        stageActivity = false;
    }

    @Override
    public boolean processCallBackQuery() {
        String callBackData = razykrashkaBot.getCallbackQuery().getData();
        if (callBackData.equals(stageInfo.getStageName() + "_en_instruction")) {
            razykrashkaBot.updateMessage(getStringMap().get("enInstruction"), (InlineKeyboardMarkup) getKeyboard());
        }

        if (callBackData.equals(stageInfo.getStageName() + "_ru_instruction")) {
            razykrashkaBot.updateMessage(getStringMap().get("ruInstruction"), (InlineKeyboardMarkup) getKeyboard());
        }
        if (callBackData.equals(stageInfo.getStageName() + "_template")) {
            razykrashkaBot.updateMessage(getStringMap().get("template"), (InlineKeyboardMarkup) getKeyboard());
        }

        if (callBackData.equals(stageInfo.getStageName() + "_example")) {
            razykrashkaBot.updateMessage(getStringMap().get("example"), (InlineKeyboardMarkup) getKeyboard());
        }
        return true;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }


}
