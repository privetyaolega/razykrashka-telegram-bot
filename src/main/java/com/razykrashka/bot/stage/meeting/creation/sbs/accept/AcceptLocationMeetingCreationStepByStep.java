package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LocationMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.MapLocationHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptLocationMeetingCreationStepByStep extends BaseMeetingCreationSBSStage {

    @Autowired
    MapLocationHelper mapLocationHelper;

    @Override
    public void handleRequest() {
        String address = razykrashkaBot.getMessageOptional().get().getText();
        Location location;
        try {
            location = mapLocationHelper.getLocation(address);
        } catch (Exception e) {
            // TODO: Create informative error message
            razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
            setActiveNextStage(LocationMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(LocationMeetingCreationSBSStage.class).handleRequest();
            return;
        }
        locationRepository.save(location);

        Meeting meeting = getMeetingInCreation();
        meeting.setLocation(location);
        meetingRepository.save(meeting);

        razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
        super.setActiveNextStage(LevelMeetingCreationSBSStage.class);
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() != null) {
            return false;
        }
        return super.getStageActivity();
    }
}