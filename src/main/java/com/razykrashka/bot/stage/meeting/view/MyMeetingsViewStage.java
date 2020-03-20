package com.razykrashka.bot.stage.meeting.view;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class MyMeetingsViewStage extends MainStage {

    @Autowired
    private MeetingMessageUtils meetingMessageUtils;

    public MyMeetingsViewStage() {
        stageInfo = StageInfo.MY_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        List<Meeting> userMeetings = meetingRepository.findAllByTelegramUser(updateHelper.getUser());

        if (userMeetings == null) {
            messageManager.sendSimpleTextMessage("NO MEETINGS :(");
        } else {
            String messageText = userMeetings.stream().skip(0).limit(20)
                    .map(meeting -> isMyMeeting(meeting, updateHelper.getUser().getTelegramId()) + meetingMessageUtils.createSingleMeetingMainInformationText(meeting))
                    .collect(Collectors.joining(getStringMap().get("delimiterLine"),
                            "\uD83D\uDCAB Найдено " + userMeetings.size() + " встреч(и)\n\n", ""));
            if (userMeetings.size() > 5) {
                //TODO: PAGINATION INLINE KEYBOARD
            }
            messageManager.sendSimpleTextMessage(messageText);
        }
    }

    private String isMyMeeting(Meeting meeting, Integer telegramId) {
        if (meeting.getTelegramUser().getTelegramId().equals(telegramId)) {
            return "**** Created By Me ****** \n";
        } else {
            return "";
        }
    }
}