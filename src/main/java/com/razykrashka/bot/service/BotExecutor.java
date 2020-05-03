package com.razykrashka.bot.service;

import com.razykrashka.bot.db.entity.telegram.TelegramMessage;
import com.razykrashka.bot.db.repo.TelegramMessageRepository;
import com.razykrashka.bot.stage.Stage;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotExecutor {

    @Autowired
    UpdateHelper updateHelper;
    @Autowired
    ApplicationContext context;
    @Autowired
    TelegramMessageRepository telegramMessageRepository;

    List<Stage> stages;
    List<Stage> activeStages;
    Stage activeStage;
    Stage undefinedStage;

    @Autowired
    public BotExecutor(@Lazy List<Stage> stages) {
        this.stages = stages;
    }

    @PostConstruct
    private void init() {
        this.undefinedStage = getContext().getBean(UndefinedStage.class);
    }

    public void execute(Update update) {
        if (!updateHelper.isMessageFromGroup()) {
            activeStages = stages.stream()
                    .filter(Stage::isStageActive)
                    .collect(Collectors.toList());

            activeStage = activeStages.isEmpty() ? undefinedStage : activeStages.get(0);

            if (update.hasCallbackQuery()) {
                activeStage.processCallBackQuery();
            } else if (update.hasMessage() || update.hasPoll()) {
                saveUpdate(update);
                activeStage.handleRequest();
            }
        }
    }

    private void saveUpdate(Update update) {
        if (!update.hasPoll()) {
            Message message = update.getMessage();
            TelegramMessage telegramMessage = TelegramMessage.builder()
                    .id(message.getMessageId())
                    .chatId(message.getChatId())
                    .fromUserId(message.getFrom().getId())
                    .botMessage(false)
                    .text(message.getText())
                    .build();
            telegramMessageRepository.save(telegramMessage);
        }
    }
}