package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
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
        if (razykrashkaBot.getRealUpdate().getMessage() != null) {
            messageManager.disableKeyboardLastBotMessage();
            setActiveNextStage(DateMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
            return true;
        }
        String ddMMyyyy = razykrashkaBot.getCallbackQuery().getData();
        LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(ddMMyyyy.substring(4)),
                Integer.parseInt(ddMMyyyy.substring(2, 4)),
                Integer.parseInt(ddMMyyyy.substring(0, 2)), 0, 0);

        Meeting meeting = getMeetingInCreation();
        meeting.setMeetingDateTime(localDateTime.withHour(0).withMinute(0));
        meetingRepository.save(meeting);

        if (!razykrashkaBot.getUser().getToGoMeetings().contains(meeting)) {
            razykrashkaBot.getUser().getToGoMeetings().add(meeting);
            razykrashkaBot.getUser().getCreatedMeetings().add(meeting);
            telegramUserRepository.save(razykrashkaBot.getUser());
        }

        razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
        return true;
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() != null
                && razykrashkaBot.getRealUpdate().getCallbackQuery().getData().contains(DateMeetingCreationSBSStage.class.getSimpleName())) {
            this.setActive(false);
            return false;
        }
        return super.getStageActivity();
    }
}