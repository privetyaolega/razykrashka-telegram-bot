package com.razykrashka.bot.ui.helpers.loading;

import com.razykrashka.bot.service.RazykrashkaBot;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
public class LoadingThread extends Thread {

    @Autowired
    private RazykrashkaBot razykrashkaBot;
    private Integer messageId;
    private Long chatId;

    @Override
    public void run() {
        try {
            chatId = razykrashkaBot.getCurrentChatId();
            log.info("Start {}'Loading Thread' for chat # {}", Thread.currentThread().getName(), chatId);

            SendMessage sendMessage = new SendMessage(chatId, ".");
            messageId = razykrashkaBot.execute(sendMessage).getMessageId();
            updateMessage("..", messageId);
            Thread.sleep(200);
            updateMessage("...", messageId);

            int count = 0;
            while (count != 3) {
                updateMessage(".  ", messageId);
                Thread.sleep(200);
                updateMessage(".. ", messageId);
                Thread.sleep(200);
                updateMessage("...", messageId);
                Thread.sleep(200);
                count++;
            }
            razykrashkaBot.execute(new DeleteMessage(chatId, messageId));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateMessage(String message, Integer messageId) {
        EditMessageText editMessageReplyMarkup = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText(message);
        try {
            razykrashkaBot.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}