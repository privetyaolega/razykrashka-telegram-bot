package com.razykrashka.bot.stage.meeting.view;

import com.razykrashka.bot.db.entity.razykrashka.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class AllMeetingViewStage extends MainStage {

    List<Meeting> modelList = new ArrayList<>();

    public AllMeetingViewStage() {
        stageInfo = StageInfo.ALL_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        modelList = StreamSupport.stream(meetingRepository.findAll().spliterator(), false).collect(Collectors.toList());
        if (modelList.size() == 0) {
            messageSender.sendSimpleTextMessage("NO MEETINGS :(");
        } else {
            String messageText = modelList.stream().skip(0).limit(20)
                    .map(model -> model.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm",
                            Locale.ENGLISH)) + "\n"
                            + "\uD83D\uDCCD" + model.getLocation().getLocationLink().toString() + "\n"
                            + model.getMeetingInfo().getSpeakingLevel().toString() + "\n"
                            + model.getMeetingInfo().getTopic() + "\n"
                            + "INFORMATION: /meeting" + model.getId())
                    .collect(Collectors.joining(getStringMap().get("delimiterLine"),
                            "\uD83D\uDCAB Найдено " + modelList.size() + " встреч(и)\n\n", ""));
            if (modelList.size() > 5) {
                //TODO: PAGINATION INLINE KEYBOARD
            }
            messageSender.sendSimpleTextMessage(messageText);
        }
    }

    @Override
    public boolean isStageActive() {
        return razykrashkaBot.getUpdate().getMessage().getText().startsWith(stageInfo.getKeyword());
    }
}
