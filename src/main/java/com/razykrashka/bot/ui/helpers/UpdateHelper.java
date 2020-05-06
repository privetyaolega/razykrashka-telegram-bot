package com.razykrashka.bot.ui.helpers;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.repo.TelegramUserRepository;
import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PROTECTED)
@Log4j2
@PropertySource(value = "classpath:/props/razykrashka.yaml", factory = YamlPropertyLoaderFactory.class)
public class UpdateHelper {

    public final static String FROM_GROUP = "fromGroup";
    @Value("${razykrashka.group.id}")
    Long groupChatId;
    final RazykrashkaBot razykrashkaBot;
    final TelegramUserRepository telegramUserRepository;

    public UpdateHelper(RazykrashkaBot razykrashkaBot, TelegramUserRepository telegramUserRepository) {
        this.razykrashkaBot = razykrashkaBot;
        this.telegramUserRepository = telegramUserRepository;
    }

    public boolean isCallBackDataContains(String string) {
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            return razykrashkaBot.getRealUpdate()
                    .getCallbackQuery().getData()
                    .contains(string);
        }
        return false;
    }

    public Long getGroupChatId() {
        return groupChatId;
    }

    public boolean isCallBackDataContains() {
        return isCallBackDataContains(getCallerClass().getSimpleName());
    }

    public boolean isCallBackDataEquals() {
        return isCallBackDataEquals(getCallerClass().getSimpleName());
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
        return "";
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
        return "";
    }

    public String getCallBackData() {
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            return razykrashkaBot.getRealUpdate().getCallbackQuery().getData();
        }
        return "";
    }

    public boolean isNewChatMember() {
        if (razykrashkaBot.getRealUpdate().hasMessage()) {
            return razykrashkaBot.getRealUpdate()
                    .getMessage()
                    .getNewChatMembers().size() != 0;
        }
        return false;
    }

    public Update getUpdate() {
        return razykrashkaBot.getRealUpdate();
    }

    public TelegramUser getUser() {
        Optional<TelegramUser> userOptional = telegramUserRepository.findById(getTelegramUserId());

        if (!userOptional.isPresent()) {
            User userTelegram = getFrom();
            TelegramUser telegramUser = TelegramUser.builder()
                    .lastName(userTelegram.getLastName())
                    .firstName(userTelegram.getFirstName())
                    .userName(userTelegram.getUserName())
                    .phoneNumber("")
                    .id(userTelegram.getId())
                    .build();
            telegramUserRepository.save(telegramUser);
            return telegramUser;
        } else {
            return userOptional.get();
        }
    }

    public User getFrom() {
        Update realUpdate = razykrashkaBot.getRealUpdate();
        if (realUpdate.hasMessage()) {
            return realUpdate.getMessage().getFrom();
        } else if (realUpdate.hasCallbackQuery()) {
            return realUpdate.getCallbackQuery().getFrom();
        } else {
            throw new RuntimeException("Can't get user from update!");
        }
    }

    public Integer getTelegramUserId() {
        Update realUpdate = razykrashkaBot.getRealUpdate();
        if (realUpdate.hasMessage()) {
            return realUpdate.getMessage().getFrom().getId();
        } else if (realUpdate.hasCallbackQuery()) {
            return realUpdate.getCallbackQuery().getFrom().getId();
        }
        return 0;
    }

    public boolean hasCallBackQuery() {
        return razykrashkaBot.getRealUpdate().hasCallbackQuery();
    }

    public boolean hasMessage() {
        return razykrashkaBot.getRealUpdate().hasMessage();
    }

    public RazykrashkaBot getBot() {
        return razykrashkaBot;
    }

    public boolean isMessageFromGroup() {
        if (hasMessage()) {
            return razykrashkaBot.getRealUpdate()
                    .getMessage()
                    .getChat().getId()
                    .equals(groupChatId);
        }
        return false;
    }

    public boolean isCallBackQueryFromGroup() {
        if (hasCallBackQuery()) {
            return razykrashkaBot.getRealUpdate()
                    .getCallbackQuery()
                    .getMessage().getChat()
                    .getId()
                    .equals(groupChatId);
        }
        return false;
    }

    public boolean isUpdateFromGroup() {
        return isCallBackDataContains(FROM_GROUP);
    }

    public int getIntDataFromCallBackQuery() {
        return Integer.parseInt(getCallBackData().replaceAll("\\D+", ""));
    }

    public int getIntDataFromMessage() {
        return Integer.parseInt(getMessageText().replaceAll("\\D+", ""));
    }
}