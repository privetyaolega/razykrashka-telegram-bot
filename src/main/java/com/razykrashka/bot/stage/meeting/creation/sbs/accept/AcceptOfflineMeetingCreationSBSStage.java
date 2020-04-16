package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.OfflineMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptOfflineMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        messageManager.deleteLastMessage()
                .deleteLastBotMessage();

        String skype = updateHelper.getMessageText();
        TelegramUser user = updateHelper.getUser();
        user.setSkypeContact(skype);
        telegramUserRepository.save(user);
        updateHelper.getBot().getContext().getBean(OfflineMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public void processCallBackQuery() {
        if (updateHelper.isCallBackDataContains("Confirm")) {
            setActiveNextStage(LevelMeetingCreationSBSStage.class);
            updateHelper.getBot().getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
        } else {
            TelegramUser user = updateHelper.getUser();
            user.setSkypeContact(null);
            telegramUserRepository.save(user);
            updateHelper.getBot().getContext().getBean(OfflineMeetingCreationSBSStage.class).handleRequest();
        }
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                && !updateHelper.isCallBackDataContains("edit");
    }
}