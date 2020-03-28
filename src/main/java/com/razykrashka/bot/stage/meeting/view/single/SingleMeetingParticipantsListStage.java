package com.razykrashka.bot.stage.meeting.view.single;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class SingleMeetingParticipantsListStage extends MainStage {

    @Autowired
    MeetingMessageUtils meetingMessageUtils;
    private Meeting meeting;

    @Override
    public boolean processCallBackQuery() {
        Integer meetingId = updateHelper.getIntegerPureCallBackData();
        meeting = meetingRepository.findMeetingById(meetingId);
        String message = meetingMessageUtils.getSingleMeetingDiscussionInfo(meeting);
        messageManager.updateMessage(message, this.getKeyboard());
        return true;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        KeyboardBuilder builder = keyboardBuilder.getKeyboard();
        if (updateHelper.getUser().getToGoMeetings().stream().anyMatch(m -> m.getId().equals(meeting.getId()))) {
            builder.setRow("Leave \uD83D\uDE30", SingleMeetingViewUnsubscribeStage.class.getSimpleName() + meeting.getId());
        } else {
            Integer participants = meeting.getParticipants().size();
            Integer participantLimit = meeting.getMeetingInfo().getParticipantLimit();
            if (participants < participantLimit) {
                builder.setRow("Join " + Emoji.ROCK_HAND, SingleMeetingViewJoinStage.class.getSimpleName() + meeting.getId());
            }
        }
        return builder
                .setRow(ImmutableMap.of(
                        "Contact " + Emoji.ONE_PERSON_SILHOUETTE, SingleMeetingViewContactStage.class.getSimpleName() + meeting.getId(),
                        "Main Info " + Emoji.FOLDER, SingleMeetingViewStage.class.getSimpleName() + meeting.getId(),
                        "Map " + Emoji.MAP, SingleMeetingViewMapStage.class.getSimpleName() + meeting.getId()))
                .build();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}