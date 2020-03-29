package com.razykrashka.bot.stage.meeting.edit.delete;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class DeleteSingleMeetingStage extends MainStage {

    @Override
    public boolean processCallBackQuery() {
        Integer id = updateHelper.getIntegerPureCallBackData();
        Meeting optionalMeeting = meetingRepository.findById(id).get();
        meetingRepository.delete(optionalMeeting);

        messageManager
                .sendRandomSticker("sad")
                .sendSimpleTextMessage("Meeting has been deleted " + Emoji.PENSIVE);
        //TODO: Send notification to each participants;
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}