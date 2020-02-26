package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.api.LoсationiqApi;
import com.razykrashka.bot.api.model.locationiq.Locationiq;
import com.razykrashka.bot.db.entity.Location;
import com.razykrashka.bot.db.entity.TelegramLinkEmbedded;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TimeMeetingCreationSBSStage;
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
            messageSender.deleteLastMessage();
        } catch (Exception e) {
            razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
            setActiveNextStage(TimeMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
            return;
        }

        Location location = Location.builder()
                .address(address)
                .latitude(Float.parseFloat(getModel.getLat()))
                .longitude(Float.parseFloat(getModel.getLon()))
                .name(getModel.getDisplayName())
                .locationLink(TelegramLinkEmbedded.builder()
                        .link("http://google.com")
                        .textLink(address)
                        .build())
                .build();
        super.getMeeting().setLocation(location);

        razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
        super.setActiveNextStage(LevelMeetingCreationSBSStage.class);
    }
}