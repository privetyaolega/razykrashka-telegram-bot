package com.razykrashka.bot.aspect;


import com.razykrashka.bot.db.repo.BlackListRepository;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Aspect
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlackListAspect {

    @Autowired
    BlackListRepository blackListRepository;
    @Autowired
    UpdateHelper updateHelper;

    @Pointcut("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))))")
    public void updateReceivedPointcut() {
    }

    @Before("updateReceivedPointcut()")
    public void userBlackListValidation() {
        int id = updateHelper.getTelegramUserId();
        if (blackListRepository.findByUserId(id).isPresent()) {
            throw new UserInBlackListException("BLACK LIST: User " + id + " is in black list! Update message is ignored! "
                    + getMessageToProcess(updateHelper.getUpdate()));
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

class UserInBlackListException extends RuntimeException {
    public UserInBlackListException(String message) {
        super(message, null, true, false);
    }
}