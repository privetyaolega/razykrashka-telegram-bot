package com.razykrashka.bot.ui.helpers.sender;

import com.google.common.collect.Iterables;
import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.telegram.TelegramMessage;
import com.razykrashka.bot.db.repo.TelegramMessageRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Log4j2
public class MessageManager extends Sender {

    @Autowired
    protected TelegramMessageRepository telegramMessageRepository;

    SendMessage sendMessage;

    public MessageManager() {
        this.sendMessage = new SendMessage();
    }

    public MessageManager sendSimpleTextMessage(String message, ReplyKeyboard keyboard) {
        Long chatId = updateHelper.getChatId();
        sendMessage = new SendMessage().setParseMode(ParseMode.HTML)
                .setChatId(chatId)
                .setText(message)
                .setReplyMarkup(keyboard)
                .disableWebPagePreview();
        try {
            Integer sentMessageId = razykrashkaBot.execute(sendMessage)
                    .getMessageId();

            boolean hasKeyboard = keyboard != null;
            TelegramMessage telegramMessage = TelegramMessage.builder()
                    .id(sentMessageId)
                    .chatId(chatId)
                    .botMessage(true)
                    .hasKeyboard(hasKeyboard)
                    .text(message)
                    .build();
            telegramMessageRepository.save(telegramMessage);

        } catch (TelegramApiException e) {
            log.error("Error during message sending!");
            log.error("FOR USER: {}", razykrashkaBot.getUser().getUserName());
            log.error("CHAT ID: {}", chatId);
            log.error("MESSAGE: {}", message);
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager disableKeyboardLastBotMessage() {
        try {
            List<TelegramMessage> telegramMessages = telegramMessageRepository.findAllByBotMessageIsTrue();
            TelegramMessage telegramMessage = Iterables.getLast(telegramMessages);
            EditMessageText editMessageReplyMarkup = new EditMessageText()
                    .setChatId(telegramMessage.getChatId())
                    .setMessageId(telegramMessage.getId())
                    .setText(telegramMessage.getText() + " ")
                    .setParseMode(ParseMode.HTML)
                    .disableWebPagePreview();
            razykrashkaBot.execute(editMessageReplyMarkup);
        } catch (Exception ignored) {
        }
        return this;
    }

    public MessageManager replyLastMessage(String textMessage, ReplyKeyboard keyboard) {
        Message message = razykrashkaBot.getRealUpdate().getMessage();
        sendMessage = new SendMessage()
                .setParseMode(ParseMode.HTML)
                .setChatId(message.getChatId())
                .setText(textMessage)
                .setReplyMarkup(keyboard)
                .setReplyToMessageId(message.getMessageId());
        Integer sentMessageId;
        try {
            sentMessageId = razykrashkaBot.execute(sendMessage)
                    .getMessageId();

            boolean hasKeyboard = keyboard != null;
            TelegramMessage telegramMessage = TelegramMessage.builder()
                    .id(sentMessageId)
                    .chatId(message.getChatId())
                    .botMessage(true)
                    .hasKeyboard(hasKeyboard)
                    .text(textMessage)
                    .build();
            telegramMessageRepository.save(telegramMessage);
        } catch (TelegramApiException e) {
            log.error("Error during message sending!");
            log.error("FOR USER: {}", razykrashkaBot.getUser().getUserName());
            log.error("MESSAGE: {}", message);
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager replyLastMessage(String textMessage) {
        return replyLastMessage(textMessage, null);
    }

    public MessageManager sendSimpleTextMessage(String message) {
        return sendSimpleTextMessage(message, null);
    }

    public MessageManager updateMessage(String message, ReplyKeyboard keyboard) {
        Message callBackMessage = razykrashkaBot.getRealUpdate().getCallbackQuery().getMessage();
        Integer messageId = telegramMessageRepository.findTop1ByChatIdAndBotMessageIsTrueOrderByIdDesc(updateHelper.getChatId()).getId();
        EditMessageText editMessageReplyMarkup = new EditMessageText()
                .setChatId(callBackMessage.getChat().getId())
                .setMessageId(messageId)
                .setText(message)
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup((InlineKeyboardMarkup) keyboard)
                .disableWebPagePreview();
        try {
            razykrashkaBot.execute(editMessageReplyMarkup);

            // TODO: Create separate method;
            boolean hasKeyboard = keyboard != null;
            TelegramMessage telegramMessage = TelegramMessage.builder()
                    .id(messageId)
                    .chatId(callBackMessage.getChat().getId())
                    .botMessage(true)
                    .hasKeyboard(hasKeyboard)
                    .text(message)
                    .build();
            telegramMessageRepository.save(telegramMessage);
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

    public MessageManager updateMessage(String message) {
        return updateMessage(message, null);
    }

    public MessageManager deleteLastMessage() {
        try {
            razykrashkaBot.execute(new DeleteMessage()
                    .setChatId(updateHelper.getChatId())
                    .setMessageId(razykrashkaBot.getRealUpdate().getMessage().getMessageId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager sendAlertMessage(String alertMessage) {
        try {
            razykrashkaBot.execute(new AnswerCallbackQuery()
                    .setCallbackQueryId(razykrashkaBot.getRealUpdate().getCallbackQuery().getId())
                    .setText(alertMessage)
                    .setShowAlert(false));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager deleteLastBotMessage() {
        TelegramMessage lastBotMessage = telegramMessageRepository.findTop1ByChatIdAndBotMessageIsTrueOrderByIdDesc(updateHelper.getChatId());
        try {
            razykrashkaBot.execute(new DeleteMessage()
                    .setChatId(lastBotMessage.getChatId())
                    .setMessageId(lastBotMessage.getId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void updateOrSendDependsOnLastMessageOwner(String textMessage, ReplyKeyboard replyKeyboard) {
        List<TelegramMessage> telegramMessages = telegramMessageRepository.findAllByBotMessageIsTrue();
        TelegramMessage telegramMessage = Iterables.getLast(telegramMessages);

        if (telegramMessage.isBotMessage()) {
            updateMessage(textMessage, replyKeyboard);
        } else {
            sendSimpleTextMessage(textMessage, replyKeyboard);
        }
    }

    public void deleteLastBotMessageIfHasKeyboard() {
        if (telegramMessageRepository.findTop1ByChatIdOrderByIdDesc(updateHelper.getChatId()).isHasKeyboard()) {
            deleteLastBotMessage();
        }
    }

    public void sendMap(Meeting meeting) {
        Location location = meeting.getLocation();
        try {
            razykrashkaBot.execute(new SendVenue()
                    .setChatId(updateHelper.getChatId())
                    .setTitle(meeting.getMeetingDateTime()
                            .format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH)))
                    .setLatitude(location.getLatitude())
                    .setLongitude(location.getLongitude())
                    .setAddress(location.getAddress()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendContact(TelegramUser user) {
        SendContact sendContact = new SendContact()
                .setLastName(user.getLastName())
                .setFirstName(user.getFirstName())
                .setPhoneNumber(user.getPhoneNumber())
                .setChatId(updateHelper.getChatId());
        try {
            razykrashkaBot.execute(sendContact);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}