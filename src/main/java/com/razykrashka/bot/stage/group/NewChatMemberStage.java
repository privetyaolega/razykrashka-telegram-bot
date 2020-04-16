package com.razykrashka.bot.stage.group;


import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.stage.MainStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewChatMemberStage extends MainStage {

    @Override
    public void handleRequest() {
        TelegramUser user = updateHelper.getUser();
        messageManager.sendMessage(new SendMessage()
                .setChatId(String.valueOf(user.getId()))
                .setText(super.getString("welcome")));
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isNewChatMember();
    }
}