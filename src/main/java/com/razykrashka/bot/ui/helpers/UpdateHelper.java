package com.razykrashka.bot.ui.helpers;

import com.razykrashka.bot.service.RazykrashkaBot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class UpdateHelper {
    private final static Integer GROUP_CHAT_ID = -454425882;

    @Autowired
    protected RazykrashkaBot razykrashkaBot;

    public boolean isCallBackDataContains(String string) {
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            return razykrashkaBot.getRealUpdate()
                    .getCallbackQuery().getData()
                    .contains(string);
        }
        return false;
    }

    public boolean isCallBackDataContains() {
        return isCallBackDataContains(getCallerClass().getSimpleName());
    }

    public boolean isCallBackDataEquals(String string) {
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            return razykrashkaBot.getRealUpdate()
                    .getCallbackQuery().getData()
                    .equals(string);
        }
        return false;
    }

    public boolean isMessageContains(String string) {
        if (razykrashkaBot.getRealUpdate().hasMessage()
                && razykrashkaBot.getRealUpdate().getMessage().hasText()) {
            return razykrashkaBot.getRealUpdate()
                    .getMessage().getText()
                    .contains(string);
        }
        return false;
    }

    public boolean isMessageTextEquals(String string) {
        if (razykrashkaBot.getRealUpdate().hasMessage()
                && razykrashkaBot.getRealUpdate().getMessage().hasText()) {
            return razykrashkaBot.getRealUpdate()
                    .getMessage().getText()
                    .equals(string);

        }
        return false;
    }

    public Long getChatId() {
        Update update = razykrashkaBot.getRealUpdate();
        if (update.hasMessage()) {
            return update.getMessage().getChat().getId();
        } else {
            return update.getCallbackQuery().getMessage().getChat().getId();
        }
    }

    public String getStringPureCallBackData() {
        String classCaller = getCallerClass().getSimpleName();
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            return razykrashkaBot.getRealUpdate()
                    .getCallbackQuery().getData()
                    .replace(classCaller, "");
        }
        throw new RuntimeException("UPDATE EXCEPTION: Update doesn't have Call Back Query!");
    }

    public Integer getIntegerPureCallBackData() {
        return Integer.valueOf(getStringPureCallBackData());
    }

    public Class getCallerClass() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(UpdateHelper.class.getName())
                    && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                try {
                    return Class.forName(ste.getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getMessageText() {
        if (razykrashkaBot.getRealUpdate().hasMessage()) {
            return razykrashkaBot.getRealUpdate().getMessage().getText();
        }
        throw new RuntimeException("UPDATE EXCEPTION: Update doesn't have message!");
    }

    public String getCallBackData() {
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            return razykrashkaBot.getRealUpdate().getCallbackQuery().getData();
        }
        throw new RuntimeException("UPDATE EXCEPTION: Update doesn't have call back query!");
    }

    public boolean hasCallBackQuery() {
        return razykrashkaBot.getRealUpdate().hasCallbackQuery();
    }
}