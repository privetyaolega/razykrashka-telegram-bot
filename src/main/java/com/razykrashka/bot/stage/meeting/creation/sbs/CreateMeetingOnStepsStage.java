package com.razykrashka.bot.stage.meeting.creation.sbs;

import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.DateMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Log4j2
@Component
public class CreateMeetingOnStepsStage extends MainStage {

    public CreateMeetingOnStepsStage() {
        stageInfo = StageInfo.CREATE_MEETING_ON_STEPS;
    }

    @Override
    public boolean processCallBackQuery() {
        messageSender.disableKeyboardLastBotMessage();
        messageSender.sendSimpleTextMessage("Start creation meeting step by step! Enjoy!");
        setActiveNextStage(DateMeetingCreationSBSStage.class);
        razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        CallbackQuery callbackQuery = razykrashkaBot.getRealUpdate().getCallbackQuery();
        if (callbackQuery != null) {
            return callbackQuery.getData().contains(this.getClass().getSimpleName());
        }
        return false;
    }
}