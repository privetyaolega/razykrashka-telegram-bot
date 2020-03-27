package com.razykrashka.bot.ui.helpers.loading;

import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

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

    public LoadingThreadV2() {
        this.loadingBar = Arrays.asList(".", "..", "...", "...\uD83D\uDCA4");
        this.intervalMillis = 200L;
        this.iterationAmount = 3;
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

        for (int i = 0; i != iterationAmount - 1; i++) {
            for (String loadingEl : loadingBar) {
                messageManager.updateMessage(loadingEl);
                threadSleep();
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