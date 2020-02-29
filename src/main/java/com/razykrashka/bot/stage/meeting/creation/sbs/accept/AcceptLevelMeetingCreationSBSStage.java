package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.MeetingInfo;
import com.razykrashka.bot.db.entity.SpeakingLevel;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.ParticipantsMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Arrays;

@Log4j2
@Component
public class AcceptLevelMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public boolean processCallBackQuery() {
        if (razykrashkaBot.getMessageOptional().isPresent()) {
            razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
            setActiveNextStage(LevelMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
            return true;
        }
        CallbackQuery callbackQuery = razykrashkaBot.getRealUpdate().getCallbackQuery();
        super.getMeeting().setMeetingInfo(MeetingInfo.builder()
                .speakingLevel(SpeakingLevel.valueOf(callbackQuery.getData().toUpperCase()))
                .build());
        messageSender.updateMessage(super.getMeetingPrettyString());
        razykrashkaBot.getContext().getBean(ParticipantsMeetingCreationSBSStage.class).handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        CallbackQuery callbackQuery = razykrashkaBot.getRealUpdate().getCallbackQuery();
        if (callbackQuery != null) {
            return Arrays.stream(SpeakingLevel.values()).anyMatch(level -> level.getLevel()
                    .equalsIgnoreCase(callbackQuery.getData())) || this.getStageActivity();
        }
        return false;
    }
}