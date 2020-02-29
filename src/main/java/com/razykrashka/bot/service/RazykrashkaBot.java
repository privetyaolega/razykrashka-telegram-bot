package com.razykrashka.bot.service;

import com.razykrashka.bot.db.entity.TelegramUser;
import com.razykrashka.bot.db.repo.TelegramUserRepository;
import com.razykrashka.bot.stage.Stage;
import com.razykrashka.bot.stage.information.UndefinedStage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RazykrashkaBot extends TelegramLongPollingBot {

    @Autowired
    protected TelegramUserRepository telegramUserRepository;

    @Value("${bot.avp256.username}")
    String botUsername;
    @Value("${bot.avp256.token}")
    String botToken;

    @Autowired
    ApplicationContext context;

    List<Stage> stages;
    Stage undefinedStage;

    Update realUpdate;
    Update update;
    Optional<Message> messageOptional;
    CallbackQuery callbackQuery;
    TelegramUser user;

    List<Stage> activeStages;

    @Autowired
    public RazykrashkaBot(@Lazy List<Stage> stages) {
        this.stages = stages;
    }

    @Override
    public void onUpdateReceived(Update update) {
        this.realUpdate = update;
        messageOptional = Optional.ofNullable(update.getMessage());
        userInit(update);
        this.undefinedStage = getContext().getBean(UndefinedStage.class);

        if (update.hasCallbackQuery()) {
            this.callbackQuery = update.getCallbackQuery();
            activeStages = stages.stream()
                    .filter(x -> callbackQuery.getData().contains(x.getStageInfo().getStageName())
                            || callbackQuery.getData().contains(x.getClass().getSimpleName())
                            || x.isStageActive()).collect(Collectors.toList());
            updateInfoLog(update.getCallbackQuery().getData());

            activeStages.get(0).processCallBackQuery();
        } else {
            this.update = update;
            activeStages = stages.stream().filter(Stage::isStageActive).collect(Collectors.toList());
            updateInfoLog(update.getMessage().getText());

            if (activeStages.size() == 0) {
                undefinedStage.handleRequest();
            } else {
                activeStages.get(0).handleRequest();
            }
        }
    }

    private void updateInfoLog(String query) {
        log.info("UPDATE: String Message to process: '{}'", query);
        log.info("UPDATE: Active stages: {}", activeStages.stream()
                .map(x -> x.getClass().getSimpleName())
                .collect(Collectors.joining(" ,", "[", "]")));
    }

    private void userInit(Update update) {
        if (!realUpdate.hasCallbackQuery()) {
            Integer id = update.getMessage().getFrom().getId();
            Optional<TelegramUser> telegramUser = telegramUserRepository.findByTelegramId(id);
            if (telegramUser.isPresent()) {
                user = telegramUser.get();
            } else {
                user = TelegramUser.builder()
                        .lastName(update.getMessage().getFrom().getLastName())
                        .firstName(update.getMessage().getFrom().getFirstName())
                        .userName(update.getMessage().getFrom().getUserName())
                        .telegramId(update.getMessage().getFrom().getId())
                        .build();
                telegramUserRepository.save(user);
            }
        }
    }

    public void sendVenue(SendVenue sendVenue) {
        sendVenue.setChatId(update.getMessage().getChat().getId());
        try {
            execute(sendVenue);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendSticker(SendSticker sticker) {
        sticker.setChatId(update.getMessage().getChat().getId());
        try {
            execute(sticker);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendContact(SendContact sendContact) {
        sendContact.setChatId(update.getMessage().getChat().getId());
        try {
            execute(sendContact);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean executeMethodCallBackQuery() {
        if (this.getRealUpdate().getCallbackQuery() != null) {
            Optional<Stage> stage = this.getStages().stream()
                    .filter(st -> this.getRealUpdate().getCallbackQuery().getData()
                            .startsWith(st.getClass().getSimpleName() + "?"))
                    .findFirst();
            if (stage.isPresent()) {
                String callBackDate = this.getRealUpdate().getCallbackQuery().getData();
                String stageName = callBackDate.split("\\?")[0];
                String methodName = callBackDate.replace(stageName + "?", "").split(":")[0];
                String[] variables = callBackDate.replace(stageName + "?" + methodName + ":", "")
                        .replace(methodName, "")
                        .split("&");

                Class<String>[] clazz = Arrays.stream(variables).map(str -> String.class)
                        .collect(Collectors.toList())
                        .stream().toArray(Class[]::new);
                try {
                    Method method = stage.get().getClass().getMethod(methodName, clazz);
                    method.invoke(stage.get(), variables);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }
}