package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.db.entity.razykrashka.meeting.SpeakingLevel;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LocationMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.ParticipantsMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Log4j2
@Component
public class AcceptLevelMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public boolean processCallBackQuery() {
        if (razykrashkaBot.getRealUpdate().hasMessage()) {
            razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
            razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
            return true;
        }
        CallbackQuery callbackQuery = razykrashkaBot.getRealUpdate().getCallbackQuery();

        MeetingInfo meetingInfo = MeetingInfo.builder()
                .speakingLevel(SpeakingLevel.valueOf(callbackQuery.getData().toUpperCase()))
                .build();
        meetingInfoRepository.save(meetingInfo);

        Meeting meeting = getMeetingInCreation();
        meeting.setMeetingInfo(meetingInfo);
        meetingRepository.save(meeting);

        razykrashkaBot.getContext().getBean(ParticipantsMeetingCreationSBSStage.class).handleRequest();
        return true;
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        boolean isEdit = false;
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            isEdit = razykrashkaBot.getRealUpdate().getCallbackQuery()
                    .getData().contains(LocationMeetingCreationSBSStage.class.getSimpleName());
        }
        return super.isStageActive() && !isEdit;
    }
}