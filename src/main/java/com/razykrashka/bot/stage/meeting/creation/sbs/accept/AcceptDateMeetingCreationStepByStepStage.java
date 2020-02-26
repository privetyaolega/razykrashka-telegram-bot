package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.DateMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TimeMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Log4j2
@Component
public class AcceptDateMeetingCreationStepByStepStage extends BaseMeetingCreationSBSStage {

    @Override
    public boolean processCallBackQuery() {
        if (razykrashkaBot.getMessageOptional().isPresent()) {
            razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
            setActiveNextStage(DateMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
            return true;
        }
        String ddMMyyyy = razykrashkaBot.getCallbackQuery().getData();
        LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(ddMMyyyy.substring(4)),
                Integer.parseInt(ddMMyyyy.substring(2, 4)),
                Integer.parseInt(ddMMyyyy.substring(0, 2)), 0, 0);
        super.getMeeting().setMeetingDateTime(localDateTime);

        super.getMeeting().setMeetingDateTime(super.getMeeting().getMeetingDateTime().withHour(0).withMinute(0));
        razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();

        return true;
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() != null) {
            if (razykrashkaBot.getRealUpdate().getCallbackQuery().getData().contains(DateMeetingCreationSBSStage.class.getSimpleName())) {
                this.setActive(false);
                return false;
            }
        }
        return super.getStageActivity();
    }
}