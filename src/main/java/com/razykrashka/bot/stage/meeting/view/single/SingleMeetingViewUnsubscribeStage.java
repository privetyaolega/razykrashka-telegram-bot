package com.razykrashka.bot.stage.meeting.view.single;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import org.springframework.stereotype.Component;

@Component
public class SingleMeetingViewUnsubscribeStage extends MainStage {

    @Override
    public boolean processCallBackQuery() {
        Integer meetingId = updateHelper.getIntegerPureCallBackData();
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Can not find meeting with id: " + meetingId));

        TelegramUser telegramUser = updateHelper.getUser();
        telegramUser.getToGoMeetings().remove(meeting);
        telegramUserRepository.save(telegramUser);

        messageManager.disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(String.format(getString("main"), meetingId));
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
