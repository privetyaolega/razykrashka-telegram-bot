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

    private final static String EN_INSTRUCTION = "Here you can create meeting using template and example :)";
    private final static String RU_INSTRUCTION = "Здесь ты можешь создать встречу.\n" +
            "Используй шаблон во вкладке 'Template' или пример на вкладке 'Example', для созданий встречи.\n" +
            "Просто копируй пример или шаблон, после чего, заполни его необходимыми данными.\n" +
            "Пожалуйста, не используй смайлы и эмоджи при форматировании встречи, а также используй формат даты и номера телефона такой, как указан в примере.\n" +
            "\n" +
            "После формирования сообщения, просто отправь его боту.\n" +
            "\n" +
            "Спасибо!";
    private final static String TEMPLATE = "NEW MEETING REQUEST\n" +
            "\n" +
            "DATE: \n" +
            "{WRITE DATE HERE IN FORMAT month.day.year  hour-minutes}\n" +
            "\n" +
            "LOCATION: \n" +
            "{WRITE HERE ADDRESS OF LOCATION IN RUSSIAN}\n" +
            "\n" +
            "MAX PEOPLE: \n" +
            "{WRITE HERE MAX AMOUNT OF ATTANDEE}\n" +
            "\n" +
            "SPEAKING LEVEL: \n" +
            "{MINIMUM SPEAKING LEVE}\n" +
            "\n" +
            "CONTACT NUMBER: \n" +
            "{WRITE HERE YOUR CONTACT NUMBER}\n" +
            "\n" +
            "TOPIC: \n" +
            "{WRITE HERE TOPIC}\n" +
            "\n" +
            "QUESTIONS:\n" +
            "{WRITE HERE QUESTIONS TO DISCUSS}";
    private final static String EXAMPLE = "NEW MEETING REQUEST:\n" +
            "\n" +
            "DATE: \n" +
            "14.01.2020 19-00\n" +
            "\n" +
            "LOCATION: \n" +
            "ул. Немига 5, Лидо\n" +
            "\n" +
            "MAX PEOPLE: \n" +
            "4\n" +
            "\n" +
            "SPEAKING LEVEL: \n" +
            "Upper-Intermediate\n" +
            "\n" +
            "CONTACT NUMBER: \n" +
            "+375295508809\n" +
            "\n" +
            "TOPIC: \n" +
            "Internet and Computers\n" +
            "\n" +
            "QUESTIONS:\n" +
            "● What are two disadvantages of using smartphones and tablets?\n" +
            "● What do you think about Cybercafes? are they still useful?\n" +
            "● Do you like a job in which you have to use computers?\n" +
            "● Do you spend too much time online?\n" +
            "● Does your mother of father know how to use a computer?\n" +
            "● Do computers make life easier?\n" +
            "● What websites do you visit in a regular basis?\n" +
            "● What do you think about the Apple?\n" +
            "● What are some advantages of using social networks?\n" +
            "● Do you visit English websites?";

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

        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow1, keyboardButtonsRow2, keyboardButtonsRow3));
        return inlineKeyboardMarkup;
    }

    @Override
    public void handleRequest() {
        if (this.processCallBackQuery()) return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(razykrashkaBot.getUpdate().getMessage().getChat().getId());
        sendMessage.setText(EN_INSTRUCTION);
        sendMessage.setReplyMarkup(getKeyboard());

        razykrashkaBot.executeBot(sendMessage);

        stageActivity = false;
    }

    @Override
    public boolean processCallBackQuery() {
        if (razykrashkaBot.getUpdate().hasCallbackQuery()) {
            String callBackData = razykrashkaBot.getUpdate().getCallbackQuery().getData();
            if (callBackData.equals(stageInfo.getStageName() + "_en_instruction")) {
                razykrashkaBot.updateMessage(EN_INSTRUCTION, (InlineKeyboardMarkup) getKeyboard());
            }

            if (callBackData.equals(stageInfo.getStageName() + "_ru_instruction")) {
                razykrashkaBot.updateMessage(RU_INSTRUCTION, (InlineKeyboardMarkup) getKeyboard());
            }
            if (callBackData.equals(stageInfo.getStageName() + "_template")) {
                razykrashkaBot.updateMessage(TEMPLATE, (InlineKeyboardMarkup) getKeyboard());
            }

            if (callBackData.equals(stageInfo.getStageName() + "_example")) {
                razykrashkaBot.updateMessage(EXAMPLE, (InlineKeyboardMarkup) getKeyboard());
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }


}
