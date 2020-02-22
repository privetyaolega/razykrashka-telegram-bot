package com.razykrashka.bot.service;

import com.razykrashka.bot.db.entity.TelegramUser;
import com.razykrashka.bot.db.repo.TelegramUserRepository;
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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Component
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RazykrashkaBot extends TelegramLongPollingBot {

    @Autowired
    protected TelegramUserRepository telegramUserRepository;

    @Value("${bot.avp256.username}")
    String botUsername;
    @Value("${bot.avp256.token}")
    String botToken;

    @Autowired
    ApplicationContext context;

    List<Stage> stages;
    Stage undefinedStage;
    Update update;
    CallbackQuery callbackQuery;
    TelegramUser user;

    @Autowired
    public RazykrashkaBot(@Lazy List<Stage> stages) {
        this.stages = stages;
    }

    @Override
    public void onUpdateReceived(Update update) {
        userInit(update);
        undefinedStage = getContext().getBean(UndefinedStage.class);
        if (update.hasCallbackQuery()) {
            this.callbackQuery = update.getCallbackQuery();
            stages.stream().filter(x -> callbackQuery.getData().contains(x.getStageInfo().getStageName()))
                    .findFirst().get().processCallBackQuery();
        } else {
            this.update = update;
            stages.stream().peek(x -> x.setMessage(update))
                    .filter(Stage::isStageActive).findFirst()
                    .orElseGet(() -> undefinedStage)
                    .handleRequest();
        }
    }

    private void userInit(Update update) {
        Optional<TelegramUser> telegramUser = telegramUserRepository.findByTelegramId(update.getMessage().getFrom().getId());
        if (telegramUser.isPresent()) {
            user = telegramUser.get();
        } else {
            user = TelegramUser.builder()
                    .lastName(update.getMessage().getFrom().getLastName())
                    .firstName(update.getMessage().getFrom().getFirstName())
                    .userName(update.getMessage().getFrom().getUserName())
                    .telegramId(update.getMessage().getFrom().getId())
                    .build();
            telegramUserRepository.save(user);
        }
    }

    public void updateMessage(String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageText editMessageReplyMarkup = new EditMessageText();
        editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChat().getId());
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageReplyMarkup.setText(text);
        editMessageReplyMarkup.setParseMode(ParseMode.MARKDOWN);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendSimpleTextMessage(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChat().getId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendVenue(SendVenue sendVenue) {
        sendVenue.setChatId(update.getMessage().getChat().getId());
        try {
            execute(sendVenue);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendSticker(SendSticker sticker) {
        sticker.setChatId(update.getMessage().getChat().getId());
        try {
            execute(sticker);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendContact(SendContact sendContact) {
        sendContact.setChatId(update.getMessage().getChat().getId());
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
}