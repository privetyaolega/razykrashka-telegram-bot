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
        if (updateHelper.hasCallBackQuery()) {
            CallbackQuery query = updateHelper.getUpdate().getCallbackQuery();
            messageManager.sendAnswerCallbackQuery(query);
        }
//        log.info("ASPECT: Sending answer call back query for {}", query.getId());
    }

    @After("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))")
    public void updateLoggingAdvice(JoinPoint joinPoint) {
        Update update = (Update) joinPoint.getArgs()[0];
        String activeStage = updateHelper.getBot().getBotExecutor()
                .getActiveStage().getClass()
                .getSimpleName().split("\\$")[0];
        log.info("ASPECT: User ID: {}. {} -> {}", getUserId(update), getMessageToProcess(update), activeStage);
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