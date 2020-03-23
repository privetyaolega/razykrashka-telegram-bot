package com.razykrashka.bot.service;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.telegram.TelegramMessage;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.db.repo.MeetingRepository;
import com.razykrashka.bot.db.repo.TelegramMessageRepository;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.stage.Stage;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource(value = "classpath:/props/razykrashka.yaml", factory = YamlPropertyLoaderFactory.class)
public class RazykrashkaBot extends TelegramLongPollingBot {

    @Value("${razykrashka.bot.username}")
    String botUsername;
    @Value("${razykrashka.bot.token}")
    String botToken;

    @Autowired
    protected TelegramMessageRepository telegramMessageRepository;
    @Autowired
    protected MeetingRepository meetingRepository;
    @Autowired
    protected CreationStateRepository creationStateRepository;
    @Autowired
    UpdateHelper updateHelper;

    @Autowired
    ApplicationContext context;
    @Autowired
    MessageManager messageManager;

    List<Stage> activeStages;
    List<Stage> stages;
    Stage undefinedStage;

    Update realUpdate;

    List<String> keyWordsList = Arrays.asList("Create Meeting", "View Meetings", "My Meetings", "Information");

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
        if (update.hasMessage() && keyWordsList.contains(update.getMessage().getText())) {
            disableCreationProgress();
        }
        activeStages = stages.stream().filter(Stage::isStageActive).collect(Collectors.toList());

        if (update.hasCallbackQuery()) {
            updateInfoLog(update.getCallbackQuery().getData());
            activeStages.get(0).processCallBackQuery();
        } else {
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

    public void disableCreationProgress() {
        Optional<Meeting> meetingOptional = meetingRepository.findByCreationStatusEqualsInProgress(updateHelper.getUser().getId());
        if (meetingOptional.isPresent()) {
            Meeting meeting = meetingOptional.get();
            CreationState creationState = meeting.getCreationState();
            creationState.setInCreationProgress(false);
            creationStateRepository.save(creationState);

            meeting.setCreationState(creationState);
            meetingRepository.save(meeting);
        }
    }

    public Long getCurrentChatId() {
        return this.getRealUpdate().getMessage() != null ? this.getRealUpdate().getMessage().getChat().getId() :
                this.getRealUpdate().getCallbackQuery().getMessage().getChat().getId();
    }
}