package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
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
        Location location;

        try {
            if (razykrashkaBot.getRealUpdate().getMessage().hasLocation()) {
                location = mapLocationHelper.getLocationByCoordinate(razykrashkaBot
                        .getRealUpdate().getMessage().getLocation());
            } else {
                String address = razykrashkaBot.getRealUpdate().getMessage().getText();
                location = mapLocationHelper.getLocation(address);
            }
        } catch (Exception e) {
            // TODO: Create informative error message
            messageManager.replyLastMessage("Nothing were found by this street. Please, clarify!");
            razykrashkaBot.getContext().getBean(LocationMeetingCreationSBSStage.class).handleRequest();
            return;
        }

        locationRepository.save(location);

        Meeting meeting = getMeetingInCreation();
        meeting.setLocation(location);
        meetingRepository.save(meeting);

        messageManager.deleteLastMessage();
        messageManager.deleteLastBotMessage();
        razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            return false;
        }
        return super.getStageActivity();
    }
}