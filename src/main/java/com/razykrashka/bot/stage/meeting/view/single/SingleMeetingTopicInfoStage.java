package com.razykrashka.bot.stage.meeting.view.single;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class SingleMeetingTopicInfoStage extends SingleMeetingViewBaseStage {

    @Override
    public boolean processCallBackQuery() {
        Integer meetingId = updateHelper.getIntegerPureCallBackData();
        meeting = meetingRepository.findMeetingById(meetingId);
        String message = meetingMessageUtils.getSingleMeetingDiscussionInfo(meeting);
        messageManager.updateMessage(message, this.getKeyboard());
        return true;
    }

    @Override
    protected List<Pair<String, String>> getMeetingInfoButtons() {
        List<Pair<String, String>> buttonList = new ArrayList<>();
        buttonList.add(Pair.of(Emoji.ONE_PERSON_SILHOUETTE, contactStage + meeting.getId()));
        buttonList.add(Pair.of(Emoji.FOLDER, mainStage + meeting.getId()));

        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            buttonList.add(Pair.of(Emoji.LOCATION, mapStage + meeting.getId()));
        }
        return buttonList;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}