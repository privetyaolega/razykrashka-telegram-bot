package com.razykrashka.bot.stage.meeting.view.single.action;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SingleMeetingViewJoinStage extends MainStage {

    @Override
    public boolean processCallBackQuery() {
        Integer meetingId = updateHelper.getIntegerPureCallBackData();
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Can not find meeting with id:" + meetingId));

        TelegramUser user = updateHelper.getUser();
        user.getToGoMeetings().add(meeting);
        telegramUserRepository.save(user);

        String message = String.format(getString("main"), meetingId);
        messageManager.disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(message);
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}