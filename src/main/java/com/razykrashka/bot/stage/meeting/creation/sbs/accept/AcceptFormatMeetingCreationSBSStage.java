package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.FormatMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LocationMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.OfflineMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptFormatMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void processCallBackQuery() {
        MeetingFormatEnum meetingFormatEnum = MeetingFormatEnum.valueOf(updateHelper.getStringPureCallBackData());
        meeting = getMeetingInCreation();
        meeting.setFormat(meetingFormatEnum);
        meetingRepository.save(meeting);

        if (meetingFormatEnum.equals(MeetingFormatEnum.OFFLINE)) {
            razykrashkaBot.getContext().getBean(LocationMeetingCreationSBSStage.class).handleRequest();
        } else {
            razykrashkaBot.getContext().getBean(OfflineMeetingCreationSBSStage.class).handleRequest();
        }
    }

    @Override
    public void handleRequest() {
        razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
        razykrashkaBot.getContext().getBean(FormatMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return (super.isStageActive()
                || updateHelper.isCallBackDataContains())
                && !updateHelper.isCallBackDataContains(EDIT);
    }
}