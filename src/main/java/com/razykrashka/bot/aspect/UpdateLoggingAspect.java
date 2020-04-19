package com.razykrashka.bot.aspect;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.razykrashka.bot.exception.StageActivityException;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource(value = {"classpath:/props/razykrashka.yaml"}, factory = YamlPropertyLoaderFactory.class)
public class UpdateLoggingAspect {

    @Value("${my.logging.path}")
    String logPath;
    @Value("${razykrashka.group.id}")
    String groupChatId;

    final UpdateHelper updateHelper;
    final MessageManager messageManager;

    public UpdateLoggingAspect(@Lazy UpdateHelper updateHelper, @Lazy MessageManager messageManager) {
        this.updateHelper = updateHelper;
        this.messageManager = messageManager;
    }

    @After("execution(* processCallBackQuery())")
    public void sendAnswerCallbackQueryAdvice() {
        if (updateHelper.hasCallBackQuery()) {
            CallbackQuery query = updateHelper.getUpdate().getCallbackQuery();
            messageManager.sendAnswerCallbackQuery(query);
        }
    }

    @After("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))")
    public void loggingUpdateAdvice(JoinPoint joinPoint) {
        Update update = (Update) joinPoint.getArgs()[0];

        List<String> activeStages = updateHelper.getBot().getBotExecutor().getActiveStages().stream()
                .map(c -> c.getClass().getSimpleName().split("\\$\\$")[0])
                .collect(Collectors.toList());

        log.info("ASPECT: User ID: {}. {} -> {}", getUserId(update), getMessageToProcess(update),
                activeStages.stream().collect(Collectors.joining(" ,", "[", "]")));

        if (activeStages.size() > 1) {
            throw new StageActivityException("More than one stage is active!");
        }
    }

    @Before("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))")
    public void initLoggingFolder(JoinPoint joinPoint) {
        Update update = (Update) joinPoint.getArgs()[0];
        String folderName;
        if (isMessageFromGroup(update)) {
            folderName = "group";
        } else {
            folderName = String.valueOf(getUserId(update));
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset();

        context.putProperty("log.folder.name", folderName);
        try {
            jc.doConfigure(new ClassPathResource("logback-spring.xml").getInputStream());
        } catch (JoranException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMessageFromGroup(Update update) {
        if (update.hasMessage()) {
            return update
                    .getMessage()
                    .getChat().getId()
                    .equals(Long.valueOf(groupChatId));
        }
        return false;
    }

    private Integer getUserId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        } else {
            return 0;
        }
    }

    private String getMessageToProcess(Update update) {
        if (update.hasMessage()) {
            return "Message: '" + update.getMessage().getText() + "'";
        } else if (update.hasCallbackQuery()) {
            return "CallBackData: '" + update.getCallbackQuery().getData() + "'";
        } else {
            return null;
        }
    }
}