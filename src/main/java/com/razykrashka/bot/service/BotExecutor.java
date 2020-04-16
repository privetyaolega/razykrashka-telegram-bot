package com.razykrashka.bot.service;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.telegram.TelegramMessage;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.db.repo.MeetingRepository;
import com.razykrashka.bot.db.repo.TelegramMessageRepository;
import com.razykrashka.bot.stage.Stage;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotExecutor {

    @Autowired
    protected TelegramMessageRepository telegramMessageRepository;
    @Autowired
    protected CreationStateRepository creationStateRepository;
    @Autowired
    protected MeetingRepository meetingRepository;
    @Autowired
    UpdateHelper updateHelper;

    @Autowired
    ApplicationContext context;
    @Autowired
    MessageManager messageManager;

    Stage activeStage;
        List<Stage> activeStages;
    List<Stage> stages;
    Stage undefinedStage;

    List<String> keyWordsList = Arrays.asList("Create Meeting", "View Meetings", "My Meetings", "Information");

    @Autowired
    public BotExecutor(@Lazy List<Stage> stages) {
        this.stages = stages;
    }

    @PostConstruct
    private void init() {
        this.undefinedStage = getContext().getBean(UndefinedStage.class);
    }

    public void execute(Update update) {
        if (keyWordsList.contains(updateHelper.getMessageText())
                || updateHelper.isUpdateFromGroup()) {
            disableCreationProgress();
        }
        activeStages = stages.stream()
                .filter(Stage::isStageActive)
                .collect(Collectors.toList());

//        activeStage = stages.stream()
//                .filter(Stage::isStageActive)
//                .findFirst()
//                .orElse(undefinedStage);

        if (update.hasCallbackQuery()) {
            activeStages.get(0).processCallBackQuery();
//            activeStage.processCallBackQuery();
        } else if (update.hasMessage() || update.hasPoll()) {
            saveUpdate(update);
            Stage activeStage = activeStages.isEmpty() ? undefinedStage : activeStages.get(0);
            activeStage.handleRequest();
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

    public void disableCreationProgress() {
        Integer id = updateHelper.getUser().getId();
        Optional<Meeting> meetingOptional = meetingRepository.findByCreationStatusEqualsInProgress(id);
        if (meetingOptional.isPresent()) {
            Meeting meeting = meetingOptional.get();
            CreationState creationState = meeting.getCreationState();
            creationState.setInCreationProgress(false);
            creationStateRepository.save(creationState);

            meeting.setCreationState(creationState);
            meetingRepository.save(meeting);
        }
    }
}