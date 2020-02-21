package com.razykrashka.bot.service;

import com.razykrashka.bot.model.telegram.TelegramUpdate;
import com.razykrashka.bot.stage.Stage;
import com.razykrashka.bot.stage.UndefinedStage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RazykrashkaBot extends TelegramLongPollingBot {

    public static final String HELLO_BUTTON = "Hello";
    public static final String START_COMMAND = "/start";
    public static final String HELP_BUTTON = "Help";

    @Getter
    @Value("${bot.avp256.username}")
    String botUsername;
    @Getter
    @Value("${bot.avp256.token}")
    String botToken;

    final TelegramUpdateService telegramUpdateService;
    //    final List<TelegramMessageHandler> telegramMessageHandlers;
    final List<Stage> stages;
    Stage undefinedStage;
    TelegramUpdate telegramUpdate;
    Update update;

    @Autowired
    private ApplicationContext context;

    @Autowired
    public RazykrashkaBot(TelegramUpdateService telegramUpdateService,
                          @Lazy List<Stage> stages) {
        this.telegramUpdateService = telegramUpdateService;
        this.stages = stages;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null) {
            try {
                telegramUpdate = telegramUpdateService.save(update);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.update = update;

        undefinedStage = getContext().getBean(UndefinedStage.class);
//        undefinedStage = stages.stream().filter(x -> x.getStageInfo().equals(StageInfo.UNDEFINED)).findFirst().get();

        if (update.hasCallbackQuery()) {
            stages.forEach(Stage::processCallBackQuery);
        }

        stages.stream()
                .peek(x -> {
                    x.setMessage(telegramUpdate);
                })
                .filter(Stage::isStageActive).findFirst()
                .orElseGet(() -> undefinedStage)
                .handleRequest();

    }

    public void updateMessage(String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageText editMessageReplyMarkup = new EditMessageText();
        editMessageReplyMarkup.setChatId(update.getCallbackQuery().getMessage().getChat().getId());
        editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageReplyMarkup.setText(text);
        editMessageReplyMarkup.setParseMode(ParseMode.MARKDOWN);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public synchronized void sendTextMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        sendMessage.setReplyMarkup(getCustomReplyKeyboardMarkup());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    private ReplyKeyboardMarkup getCustomReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(HELLO_BUTTON));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(HELP_BUTTON));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public void sendSimpleTextMessage(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(telegramUpdate.getMessage().getChat().getId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendVenue(SendVenue sendVenue) {
        sendVenue.setChatId(telegramUpdate.getMessage().getChat().getId());
        try {
            execute(sendVenue);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendSticker(SendSticker sticker) {
        sticker.setChatId(telegramUpdate.getMessage().getChat().getId());
        try {
            execute(sticker);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendContact(SendContact sendContact) {
        sendContact.setChatId(telegramUpdate.getMessage().getChat().getId());
        try {
            execute(sendContact);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void executeBot(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public ApplicationContext getContext() {
        return context;
    }

    public Update getUpdate() {
        return update;
    }
}