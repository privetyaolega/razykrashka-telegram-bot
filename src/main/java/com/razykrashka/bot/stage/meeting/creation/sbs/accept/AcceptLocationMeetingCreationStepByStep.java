package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.api.LoсationiqApi;
import com.razykrashka.bot.api.model.locationiq.Locationiq;
import com.razykrashka.bot.db.entity.Location;
import com.razykrashka.bot.db.entity.TelegramLinkEmbedded;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LocationMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptLocationMeetingCreationStepByStep extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {

        Locationiq getModel;
        String address;
        try {
            address = razykrashkaBot.getMessageOptional().get().getText();
            getModel = LoсationiqApi.getLocationModel(address + ", Минск").stream()
                    .filter(x -> x.getDisplayName().contains("Minsk"))
                    .findFirst().get();
        } catch (Exception e) {
            // TODO: Create informative error message
            razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
            setActiveNextStage(LocationMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(LocationMeetingCreationSBSStage.class).handleRequest();
            return;
        }

        Location location = Location.builder()
                .address(address)
                .latitude(Float.parseFloat(getModel.getLat()))
                .longitude(Float.parseFloat(getModel.getLon()))
                .name(getModel.getDisplayName())
                .locationLink(TelegramLinkEmbedded.builder()
                        // TODO: Create link to google map
                        .link("http://google.com")
                        .textLink(address)
                        .build())
                .build();
        super.getMeeting().setLocation(location);

        razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
        super.setActiveNextStage(LevelMeetingCreationSBSStage.class);
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() != null) {
            return false;
        }
        return super.getStageActivity();
    }
}