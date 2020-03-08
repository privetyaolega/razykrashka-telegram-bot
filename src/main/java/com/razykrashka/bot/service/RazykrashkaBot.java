package com.razykrashka.bot.service;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.telegram.TelegramMessage;
import com.razykrashka.bot.db.repo.TelegramMessageRepository;
import com.razykrashka.bot.db.repo.TelegramUserRepository;
import com.razykrashka.bot.stage.Stage;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
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
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RazykrashkaBot extends TelegramLongPollingBot {

    @Value("${bot.avp256.username}")
    String botUsername;
    @Value("${bot.avp256.token}")
    String botToken;

    @Autowired
    protected TelegramUserRepository telegramUserRepository;
    @Autowired
    protected TelegramMessageRepository telegramMessageRepository;
    @Autowired
    ApplicationContext context;
    @Autowired
    MessageManager messageManager;

    List<Stage> activeStages;
    List<Stage> stages;
    Stage undefinedStage;

    Update realUpdate;
    TelegramUser user;

    List<String> keyWordsList = Arrays.asList("Create Meeting", "View Meetings", "View My Meetings", "Information :P");

    @Autowired
    public RazykrashkaBot(@Lazy List<Stage> stages) {
        this.stages = stages;
    }

    @PostConstruct
    private void init() {
        this.undefinedStage = getContext().getBean(UndefinedStage.class);
    }

    @Override
    public void onUpdateReceived(Update update) {
        this.realUpdate = update;
        userInit();

        activeStages = stages.stream().filter(Stage::isStageActive).collect(Collectors.toList());

        if (update.hasCallbackQuery()) {
            updateInfoLog(update.getCallbackQuery().getData());
            activeStages.get(0).processCallBackQuery();
        } else {
            if (keyWordsList.contains(update.getMessage().getText())) {
                this.getStages().forEach(stage -> stage.setActive(false));
            }
            saveUpdate();
            messageManager.disableKeyboardLastBotMessage();
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

    private void userInit() {
        if (!realUpdate.hasCallbackQuery()) {
            Integer id = realUpdate.getMessage().getFrom().getId();
            Optional<TelegramUser> telegramUser = telegramUserRepository.findByTelegramId(id);
            if (telegramUser.isPresent()) {
                user = telegramUser.get();
            } else {
                User userTelegram = realUpdate.getMessage().getFrom();
                user = TelegramUser.builder()
                        .lastName(userTelegram.getLastName())
                        .firstName(userTelegram.getFirstName())
                        .userName(userTelegram.getUserName())
                        .telegramId(userTelegram.getId())
                        .build();
                telegramUserRepository.save(user);
            }
        }
    }

    private void saveUpdate() {
        Message message = this.getRealUpdate().getMessage();
        TelegramMessage telegramMessage = TelegramMessage.builder()
                .id(message.getMessageId())
                .chatId(message.getChatId())
                .fromUserId(message.getFrom().getId())
                .botMessage(false)
                .text(message.getText())
                .build();
        telegramMessageRepository.save(telegramMessage);
    }

    public void sendSticker(SendSticker sticker) {
        sticker.setChatId(getCurrentChatId());
        try {
            execute(sticker);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Long getCurrentChatId() {
        return this.getRealUpdate().getMessage() != null ? this.getRealUpdate().getMessage().getChat().getId() :
                this.getRealUpdate().getCallbackQuery().getMessage().getChat().getId();
    }
}