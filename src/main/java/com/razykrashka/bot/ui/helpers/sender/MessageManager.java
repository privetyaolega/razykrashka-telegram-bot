package com.razykrashka.bot.ui.helpers.sender;

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
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        SendMessage sendMessage = new SendMessage()
                .setParseMode(ParseMode.HTML)
                .setChatId(chatId)
                .setText(message)
                .setReplyMarkup(keyboard)
                .disableWebPagePreview();
        try {
            Integer sentMessageId = razykrashkaBot.execute(sendMessage)
                    .getMessageId();

            TelegramMessage telegramMessage = TelegramMessage.builder()
                    .id(sentMessageId)
                    .chatId(chatId)
                    .botMessage(true)
                    .hasKeyboard(keyboard != null)
                    .text(message)
                    .build();
            telegramMessageRepository.save(telegramMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager disableKeyboardLastBotMessage() {
        String chatId = String.valueOf(updateHelper.getChatId());
        return disableKeyboardLastBotMessage(chatId);
    }

    public MessageManager disableKeyboardLastBotMessage(String chatId) {
        TelegramMessage telegramMessage = telegramMessageRepository
                .findTop1ByChatIdAndBotMessageIsTrueOrderByIdDesc(Long.valueOf(chatId));

        if (telegramMessage == null) {
            return this;
        }

        if (telegramMessage.isHasKeyboard()) {
            EditMessageText editMessageReplyMarkup = new EditMessageText()
                    .setChatId(chatId)
                    .setMessageId(telegramMessage.getId())
                    .setText(telegramMessage.getText() + " ")
                    .setParseMode(ParseMode.HTML)
                    .disableWebPagePreview();
            return send(editMessageReplyMarkup);
        }
        return this;
    }

    public MessageManager replyLastMessage(String textMessage, ReplyKeyboard keyboard) {
        Message message = razykrashkaBot.getRealUpdate().getMessage();
        SendMessage sendMessage = new SendMessage()
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
        Long chatId = updateHelper.getChatId();
        Integer messageId = telegramMessageRepository.findTop1ByChatIdAndBotMessageIsTrueOrderByIdDesc(chatId).getId();
        EditMessageText editMessageReplyMarkup = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText(message)
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup((InlineKeyboardMarkup) keyboard)
                .disableWebPagePreview();
        send(editMessageReplyMarkup);
        saveMessage(editMessageReplyMarkup);
        return this;
    }

    private void saveMessage(EditMessageText message) {
        boolean hasKeyboard = message.getReplyMarkup() != null;
        TelegramMessage telegramMessage = TelegramMessage.builder()
                .id(message.getMessageId())
                .chatId(Long.valueOf(message.getChatId()))
                .botMessage(true)
                .hasKeyboard(hasKeyboard)
                .text(message.getText())
                .build();
        telegramMessageRepository.save(telegramMessage);
    }

    public MessageManager updateMessage(String message) {
        return updateMessage(message, null);
    }

    public MessageManager deleteLastMessage() {
        Integer id = razykrashkaBot.getRealUpdate().getMessage().getMessageId();
        DeleteMessage deleteMessage = new DeleteMessage()
                .setChatId(updateHelper.getChatId())
                .setMessageId(id);
        return send(deleteMessage);
    }

    public MessageManager sendAlertMessage(String alertMessage) {
        return sendAlertMessage(alertMessage, false);
    }

    public MessageManager sendAlertMessage(String alertMessage, boolean showAlert) {
        String id = razykrashkaBot.getRealUpdate().getCallbackQuery().getId();
        AnswerCallbackQuery callbackQuery = new AnswerCallbackQuery()
                .setCallbackQueryId(id)
                .setText(alertMessage)
                .setShowAlert(showAlert);
        return send(callbackQuery);
    }

    public MessageManager deleteLastBotMessage() {
        TelegramMessage lastBotMessage = telegramMessageRepository.findTop1ByChatIdAndBotMessageIsTrueOrderByIdDesc(updateHelper.getChatId());
        DeleteMessage deleteMessage = new DeleteMessage()
                .setChatId(lastBotMessage.getChatId())
                .setMessageId(lastBotMessage.getId());
        return send(deleteMessage);
    }

    private MessageManager send(BotApiMethod botApiMethod) {
        try {
            razykrashkaBot.execute(botApiMethod);
        } catch (TelegramApiException ignored) {
            ignored.printStackTrace();
        }
        return this;
    }

    public void updateOrSendDependsOnLastMessageOwner(String textMessage, ReplyKeyboard replyKeyboard) {
        TelegramMessage telegramMessage = telegramMessageRepository.findTop1ByChatIdOrderByIdDesc(updateHelper.getChatId());
        if (telegramMessage.isBotMessage()) {
            updateMessage(textMessage, replyKeyboard);
        } else {
            disableKeyboardLastBotMessage();
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
        SendVenue venue = new SendVenue()
                .setChatId(updateHelper.getChatId())
                .setTitle(meeting.getMeetingDateTime()
                        .format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH)))
                .setLatitude(location.getLatitude())
                .setLongitude(location.getLongitude())
                .setAddress(location.getAddress());
        return send(venue);
    }

    public MessageManager sendContact(TelegramUser user) {
        SendContact sendContact = new SendContact()
                .setLastName(user.getLastName())
                .setFirstName(user.getFirstName())
                .setPhoneNumber(user.getPhoneNumber())
                .setChatId(updateHelper.getChatId());
        return send(sendContact);
    }

    public MessageManager sendSticker(String stickerFileName, long chatId) {
        try {
            File stickerFile = new ClassPathResource("bot/stickers/" + stickerFileName).getFile();
            SendSticker sendSticker = new SendSticker()
                    .setSticker(stickerFile)
                    .setChatId(chatId);
            razykrashkaBot.execute(sendSticker);
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager sendSticker(String stickerFileName) {
        return sendSticker(stickerFileName, updateHelper.getChatId());
    }

    public MessageManager sendRandomSticker(String folder, long chatId) {
        try (Stream<Path> paths = Files.walk(Paths.get(new ClassPathResource("bot/stickers/" + folder).getURI()))) {
            List<String> collect = paths.filter(Files::isRegularFile)
                    .map(x -> x.getFileName().toString())
                    .collect(Collectors.toList());
            int randomNumber = new Random().nextInt(collect.size());
            sendSticker(File.separator + folder + File.separator + collect.get(randomNumber), chatId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager sendRandomSticker(String folder) {
        return sendRandomSticker(folder, updateHelper.getChatId());
    }

    public MessageManager sendAnswerCallbackQuery(CallbackQuery callbackQuery) {
        AnswerCallbackQuery query = new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQuery.getId());
        return send(query);
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

    public MessageManager sendAnimation(String path, String label) {
        try {
            File file = new ClassPathResource(path).getFile();
            SendAnimation photo = new SendAnimation()
                    .setCaption(label)
                    .setParseMode(ParseMode.HTML)
                    .setChatId(updateHelper.getChatId())
                    .setAnimation(file);
            razykrashkaBot.execute(photo);
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MessageManager sendAnimation(String path) {
        return sendAnimation(path, null);
    }

    public MessageManager sendChatAction(ActionType actionType) {
        SendChatAction action = new SendChatAction()
                .setChatId(updateHelper.getChatId())
                .setAction(actionType);
        return send(action);
    }

    public MessageManager sendInvoice() {
        LabeledPrice labeledPrice = new LabeledPrice();
        labeledPrice.setAmount(50000);
        labeledPrice.setLabel("Test Label");

        SendInvoice invoice = new SendInvoice()
                .setChatId(Math.toIntExact(updateHelper.getChatId()))
                .setTitle("Test Title")
                .setDescription("Test Description")
                .setPayload("Test Payload")
                .setProviderToken("")
                .setStartParameter("start")
                .setCurrency("RUB")
                .setPrices(Arrays.asList(labeledPrice));

        try {
            razykrashkaBot.execute(invoice);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }
}