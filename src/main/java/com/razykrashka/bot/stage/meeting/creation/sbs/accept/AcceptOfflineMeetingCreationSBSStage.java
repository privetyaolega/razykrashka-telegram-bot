package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.repo.LocationRepository;
import com.razykrashka.bot.exception.IncorrectInputDataFormatException;
import com.razykrashka.bot.exception.YandexMapApiException;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.OfflineMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.LocationHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AcceptOfflineMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    final static String LOCATION_REGEXP = "[\\s\\S]{0,19}[A-Za-z\\u0400-\\u04FF]{3,10} \\d{0,4}[\\s\\S]{0,8}";
    final LocationHelper locationHelper;
    final LocationRepository locationRepository;

    public AcceptOfflineMeetingCreationSBSStage(LocationHelper locationHelper, LocationRepository locationRepository) {
        this.locationHelper = locationHelper;
        this.locationRepository = locationRepository;
    }

    @Override
    public void handleRequest() {
        Location location;

        try {
            if (razykrashkaBot.getRealUpdate().getMessage().hasLocation()) {
                location = locationHelper.getLocationByCoordinate(razykrashkaBot
                        .getRealUpdate().getMessage().getLocation());
            } else {
                String address = razykrashkaBot.getRealUpdate().getMessage()
                        .getText().trim()
                        .replaceAll(" +", " ");

                if (!address.matches(LOCATION_REGEXP)) {
                    throw new IncorrectInputDataFormatException(address + ": address doesn't match regexp");
                }
                location = locationHelper.getLocation(address);
            }
        } catch (Exception e) {
            messageManager
                    .disableKeyboardLastBotMessage()
                    .replyLastMessage(getString("error"));
            razykrashkaBot.getContext().getBean(OfflineMeetingCreationSBSStage.class).handleRequest(false);
            return;
        }

        locationRepository.save(location);

        Meeting meeting = getMeetingInCreation();
        meeting.setLocation(location);
        meetingRepository.save(meeting);

        messageManager.deleteLastMessage()
                .deleteLastBotMessage();
        razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return !updateHelper.hasCallBackQuery()
                && super.isStageActive();
    }
}