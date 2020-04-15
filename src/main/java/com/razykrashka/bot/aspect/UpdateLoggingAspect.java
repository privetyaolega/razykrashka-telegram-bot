package com.razykrashka.bot.aspect;


import com.razykrashka.bot.exception.StageActivityException;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateLoggingAspect {

    final UpdateHelper updateHelper;
    final MessageManager messageManager;

    public UpdateLoggingAspect(@Lazy UpdateHelper updateHelper, @Lazy MessageManager messageManager) {
        this.updateHelper = updateHelper;
        this.messageManager = messageManager;
    }

    @After("execution(* processCallBackQuery())")
    public void sendAnswerCallbackQueryAdvice() {
        CallbackQuery query = updateHelper.getUpdate().getCallbackQuery();
        messageManager.sendAnswerCallbackQuery(query);
//        log.info("ASPECT: Sending answer call back query for {}", query.getId());
    }

    @After("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))")
    public void updateLoggingAdvice(JoinPoint joinPoint) {
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