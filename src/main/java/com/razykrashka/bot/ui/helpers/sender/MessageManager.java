package com.razykrashka.bot.ui.helpers.sender;

import com.google.common.collect.Iterables;
import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.poll.TelegramPoll;
import com.razykrashka.bot.db.entity.razykrashka.poll.TelegramPollOption;
import com.razykrashka.bot.db.entity.telegram.TelegramMessage;
import com.razykrashka.bot.db.repo.PollOptionRepository;
import com.razykrashka.bot.db.repo.PollRepository;
import com.razykrashka.bot.db.repo.TelegramMessageRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class MessageManager extends Sender {

    @Autowired
    TelegramMessageRepository telegramMessageRepository;
    @Autowired
    PollRepository pollRepository;
    @Autowired
    PollOptionRepository pollOptionRepository;
    SendMessage sendMessage;

    public MessageManager() {
        this.sendMessage = new SendMessage();
    }

    public MessageManager sendMessage(SendMessage sendMessage) {
        try {
            Integer sentMessageId = razykrashkaBot
                    .execute(sendMessage)
                    .getMessageId();

            TelegramMessage telegramMessage = TelegramMessage.builder()
                    .id(sentMessageId)
                    .chatId(Long.valueOf(sendMessage.getChatId()))
                    .botMessage(true)
                    .hasKeyboard(false)
                    .text(sendMessage.getText())
                    .build();
            telegramMessageRepository.save(telegramMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
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
            log.error("FOR USER: {}", updateHelper.getUser().getUserName());
            log.error("CHAT ID: {}", chatId);
            log.error("MESSAGE: {}", message);
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager disableKeyboardLastBotMessage() {
        String chatId = String.valueOf(updateHelper.getChatId());
        return disableKeyboardLastBotMessage(chatId);
    }

    public MessageManager disableKeyboardLastBotMessage(String chatId) {
        try {
            List<TelegramMessage> telegramMessages = telegramMessageRepository.findAllByBotMessageIsTrueAndChatIdEquals(Long.valueOf(chatId));
            TelegramMessage telegramMessage = Iterables.getLast(telegramMessages);
            EditMessageText editMessageReplyMarkup = new EditMessageText()
                    .setChatId(updateHelper.getChatId())
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
            log.error("FOR USER: {}", updateHelper.getUser().getUserName());
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
            log.error("FOR USER: {}", updateHelper.getUser().getUserName());
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
        TelegramMessage telegramMessage = telegramMessageRepository.findTop1ByChatIdOrderByIdDesc(updateHelper.getChatId());

        if (telegramMessage.isBotMessage()) {
            updateMessage(textMessage, replyKeyboard);
        } else {
            sendSimpleTextMessage(textMessage, replyKeyboard);
        }
    }

    public MessageManager deleteLastBotMessageIfHasKeyboard() {
        if (telegramMessageRepository.findTop1ByChatIdOrderByIdDesc(updateHelper.getChatId()).isHasKeyboard()) {
            deleteLastBotMessage();
        }
        return this;
    }

    public MessageManager sendMap(Meeting meeting) {
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
        return this;
    }

    public MessageManager sendContact(TelegramUser user) {
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
        return this;
    }

    public MessageManager sendSticker(String stickerFileName) {
        try {
            File stickerFile = new ClassPathResource("stickers/" + stickerFileName).getFile();
            SendSticker sendSticker = new SendSticker()
                    .setSticker(stickerFile)
                    .setChatId(updateHelper.getChatId());
            razykrashkaBot.execute(sendSticker);
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void sendAnswerCallbackQuery(CallbackQuery callbackQuery) {
        try {
            razykrashkaBot.execute(new AnswerCallbackQuery()
                    .setCallbackQueryId(callbackQuery.getId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public MessageManager sendPoll(String chatId, String question, List<String> options) {
        SendPoll sendPoll = new SendPoll()
                .setChatId(chatId)
                .setAnonymous(true)
                .setAllowMultipleAnswers(true)
                .setQuestion(question)
                .setOptions(options);
        try {
            Poll poll = razykrashkaBot.execute(sendPoll).getPoll();
            TelegramPoll telegramPollEntity = new TelegramPoll(poll);
            pollRepository.save(telegramPollEntity);

            Set<TelegramPollOption> telegramPollOptions = new HashSet<>();
            for (PollOption po : poll.getOptions()) {
                TelegramPollOption o = TelegramPollOption.builder()
                        .textOption(po.getText())
                        .count(po.getVoterCount())
                        .build();
                telegramPollOptions.add(o);
                o.setPoll(telegramPollEntity);
                pollOptionRepository.save(o);
            }
            telegramPollEntity.setTelegramPollOptions(telegramPollOptions);
            pollRepository.save(telegramPollEntity);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }
}