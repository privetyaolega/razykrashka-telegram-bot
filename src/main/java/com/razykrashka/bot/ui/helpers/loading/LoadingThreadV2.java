package com.razykrashka.bot.ui.helpers.loading;

import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.ActionType;

import java.util.Arrays;
import java.util.List;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoadingThreadV2 extends Thread {

    @Autowired
    MessageManager messageManager;
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

        this.intervalMillis = 50L;
        this.iterationAmount = 1;
        this.fixIterationLoading = fixIterationLoading;
    }

    @Override
    public void run() {
        messageManager.disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(loadingBar.get(0));
        threadSleep();

        for (int i = 1; i < loadingBar.size(); i++) {
            messageManager.updateMessage(loadingBar.get(i));
            threadSleep();
        }

        if (fixIterationLoading) {
            for (int i = 0; i != iterationAmount - 1; i++) {
                for (String loadingEl : loadingBar) {
                    messageManager.updateMessage(loadingEl)
                            .sendChatAction(ActionType.TYPING);
                    threadSleep();
                }
            }
        } else {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    for (String loadingEl : loadingBar) {
                        messageManager.updateMessage(loadingEl)
                                .sendChatAction(ActionType.UPLOADVIDEO);
                        Thread.sleep(intervalMillis);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void threadSleep() {
        try {
            Thread.sleep(intervalMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}