package com.razykrashka.bot.ui.helpers.sender;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Log4j2
public class MessageSender extends Sender {

    SendMessage sendMessage;
    Integer lastBotMessageId;

    public MessageSender() {
        this.sendMessage = new SendMessage();
    }

    public MessageSender sendSimpleTextMessage(String message, ReplyKeyboard keyboard) {
        Long chatId = razykrashkaBot.getUpdate().getMessage().getChat().getId();
        sendMessage = new SendMessage().setParseMode(ParseMode.HTML)
                .setChatId(chatId)
                .setText(message)
                .setReplyMarkup(keyboard)
                .disableWebPagePreview();
        try {
            lastBotMessageId = razykrashkaBot.execute(sendMessage)
                    .getMessageId();
        } catch (TelegramApiException e) {
            log.error("Error during message sending!");
            log.error("FOR USER: {}", razykrashkaBot.getUser().getUserName());
            log.error("CHAT ID: {}", chatId);
            log.error("MESSAGE: {}", message);
            e.printStackTrace();
        }
        return this;
    }

    public MessageSender sendSimpleTextMessage(String message) {
        return sendSimpleTextMessage(message, null);
    }

    public MessageSender updateMessage(String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        Message callBackMessage = razykrashkaBot.getCallbackQuery().getMessage();
        EditMessageText editMessageReplyMarkup = new EditMessageText()
                .setChatId(callBackMessage.getChat().getId())
                .setMessageId(lastBotMessageId)
                .setText(message)
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup(inlineKeyboardMarkup)
                .disableWebPagePreview();
        try {
            razykrashkaBot.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Error during message sending!");
            log.error("FOR USER: {}", razykrashkaBot.getUser().getUserName());
            log.error("CHAT ID: {}", callBackMessage.getChat().getId());
            log.error("MESSAGE: {}", message);
            log.error("MESSAGE ID: {}", callBackMessage.getMessageId());
            e.printStackTrace();
        }
        return this;
    }

    public MessageSender updateMessage(String message) {
        return updateMessage(message, null);
    }

    public MessageSender deleteLastMessage() {
        try {
            razykrashkaBot.execute(new DeleteMessage()
                    .setChatId(razykrashkaBot.getUpdate().getMessage().getChatId())
                    .setMessageId(razykrashkaBot.getUpdate().getMessage().getMessageId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageSender sendAlertMessage(String alertMessage) {
        try {
            razykrashkaBot.execute(new AnswerCallbackQuery()
                    .setCallbackQueryId(razykrashkaBot.getCallbackQuery().getId())
                    .setText(alertMessage)
                    .setShowAlert(false));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageSender deleteLastBotMessage() {
        try {
            razykrashkaBot.execute(new DeleteMessage()
                    .setChatId(razykrashkaBot.getUpdate().getMessage().getChatId())
                    .setMessageId(lastBotMessageId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }
}