package com.razykrashka.bot.stage.meeting.view.single;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
public class SingleMeetingViewContactStage extends MainStage {

    @Override
    public boolean processCallBackQuery() {
        Integer meetingId = updateHelper.getIntegerPureCallBackData();
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Can not find meeting with id: " + meetingId));
        Optional<TelegramUser> user = Optional.ofNullable(meeting.getTelegramUser());
        user.ifPresent(x -> messageManager.sendContact(x));
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}