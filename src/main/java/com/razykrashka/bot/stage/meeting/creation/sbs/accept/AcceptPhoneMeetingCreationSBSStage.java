package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.start.VerifyMeetingStateSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@Component
public class AcceptPhoneMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        Update update = updateHelper.getUpdate();

        if (update.hasMessage() && update.getMessage().hasContact()) {
            TelegramUser user = updateHelper.getUser();
            user.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
            telegramUserRepository.save(user);

            String message = getString("accept");
            messageManager.sendSimpleTextMessage(message, getMainKeyboard());
            updateHelper.getBot().getContext().getBean(VerifyMeetingStateSBSStage.class).processCallBackQuery();
        } else {
            meetingRepository.findByCreationStatusEqualsInProgress(updateHelper.getTelegramUserId())
                    .ifPresent(m -> meetingRepository.delete(m));
            messageManager.replyLastMessage("Ooopss! Sorry, but it's unknown command \uD83E\uDD74\n" +
                            "\n" +
                            "List of available commands find here:\n" +
                            "/help✨",
                    getMainKeyboard());
        }
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive();
    }
}