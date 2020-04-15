package com.razykrashka.bot.ui.helpers.loading;

import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoadingThreadV2 extends Thread {

    @Autowired
    RazykrashkaBot bot;
    @Autowired
    MessageManager messageManager;

    Integer messageId;
    List<String> loadingBar;
    Long intervalMillis;
    int iterationAmount;
    boolean fixIterationLoading;

    public LoadingThreadV2(boolean fixIterationLoading) {
        String blank = "⠀";
        this.loadingBar = Arrays.asList(
                "\uD83D\uDE80⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀✨",
                "⠀\uD83D\uDE80⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀✨",
                "⠀⠀\uD83D\uDE80⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀✨",
                "⠀⠀⠀\uD83D\uDE80⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀✨",
                "⠀⠀⠀⠀\uD83D\uDE80⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀✨",
                "⠀⠀⠀⠀⠀\uD83D\uDE80⠀⠀⠀⠀⠀⠀⠀⠀⠀✨",
                "⠀⠀⠀⠀⠀⠀\uD83D\uDE80⠀⠀⠀⠀⠀⠀⠀⠀✨",
                "⠀⠀⠀⠀⠀⠀⠀\uD83D\uDE80⠀⠀⠀⠀⠀⠀⠀✨",
                "⠀⠀⠀⠀⠀⠀⠀⠀\uD83D\uDE80⠀⠀⠀⠀⠀⠀✨",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀\uD83D\uDE80⠀⠀⠀⠀⠀✨",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\uD83D\uDE80⠀⠀⠀⠀✨",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\uD83D\uDE80⠀⠀⠀✨",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\uD83D\uDE80⠀⠀✨",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\uD83D\uDE80⠀✨",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\uD83D\uDE80✨");

        this.intervalMillis = 10L;
        this.iterationAmount = 1;
        this.fixIterationLoading = fixIterationLoading;
    }

    @Override
    public void run() {
        messageManager.disableKeyboardLastBotMessage();
        messageId = sendSimpleTextMessage(loadingBar.get(0));

        threadSleep();

        for (int i = 1; i < loadingBar.size(); i++) {
            updateMessage(loadingBar.get(i), messageId);
            threadSleep();
        }

        if (fixIterationLoading) {
            for (int i = 0; i != iterationAmount - 1; i++) {
                for (String loadingEl : loadingBar) {
                    updateMessage(loadingEl, messageId);
                    SendChatAction action = new SendChatAction()
                            .setChatId(getChatId())
                            .setAction(ActionType.TYPING);
                    send(action);
                    threadSleep();
                }
            }
            deleteMessage(messageId);
        } else {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    for (String loadingEl : loadingBar) {
                        updateMessage(loadingEl, messageId);
                        SendChatAction action = new SendChatAction()
                                .setChatId(getChatId())
                                .setAction(ActionType.UPLOADDOCUMENT);
                        try {
                            bot.execute(action);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        Thread.sleep(intervalMillis);
                    }
                }
            } catch (InterruptedException e) {
                deleteMessage(messageId);
                Thread.currentThread().interrupt();
            }
        }
    }

    public void updateMessage(String message, Integer messageId) {
        EditMessageText editMessageReplyMarkup = new EditMessageText()
                .setChatId(getChatId())
                .setMessageId(messageId)
                .setText(message)
                .setParseMode(ParseMode.HTML)
                .disableWebPagePreview();
        send(editMessageReplyMarkup);
    }

    public void deleteMessage(Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage()
                .setChatId(getChatId())
                .setMessageId(messageId);
        send(deleteMessage);
    }

    public Integer sendSimpleTextMessage(String message) {
        Long chatId = getChatId();
        SendMessage sendMessage = new SendMessage()
                .setParseMode(ParseMode.HTML)
                .setChatId(chatId)
                .setText(message)
                .disableWebPagePreview();
        try {
            Integer sentMessageId = bot.execute(sendMessage)
                    .getMessageId();
            return sentMessageId;
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }

    public Long getChatId() {
        Update update = bot.getRealUpdate();
        if (update.hasMessage()) {
            return update.getMessage().getChat().getId();
        } else {
            return update.getCallbackQuery().getMessage().getChat().getId();
        }
    }

    private LoadingThreadV2 send(BotApiMethod botApiMethod) {
        try {
            bot.execute(botApiMethod);
        } catch (TelegramApiException ignored) {
        }
        return this;
    }

    public void threadSleep() {
        try {
            Thread.sleep(intervalMillis);
        } catch (InterruptedException e) {
            deleteMessage(messageId);
            e.printStackTrace();
        }
    }
}