package com.razykrashka.bot.stage.meeting.view.single;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.single.SingleMeetingParticipantsListStage;
import com.razykrashka.bot.stage.meeting.view.single.SingleMeetingViewContactStage;
import com.razykrashka.bot.stage.meeting.view.single.SingleMeetingViewJoinStage;
import com.razykrashka.bot.stage.meeting.view.single.SingleMeetingViewMapStage;
import com.razykrashka.bot.stage.meeting.view.single.SingleMeetingViewUnsubscribeStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class SingleMeetingViewStage extends MainStage {

    @Autowired
    private MeetingMessageUtils meetingMessageUtils;
    private Meeting meeting;

    public SingleMeetingViewStage() {
        stageInfo = StageInfo.SINGLE_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        Integer id = getMeetingId();

        meeting = meetingRepository.findById(id).get();

        String messageText = meetingMessageUtils.createSingleMeetingFullText(meeting);
        messageManager.updateOrSendDependsOnLastMessageOwner(messageText, this.getKeyboard());
    }

    private Integer getMeetingId() {
        if (updateHelper.getMessageText() != null) {
            return Integer.valueOf(updateHelper.getMessageText()
                    .replace(this.getStageInfo().getKeyword(), ""));
        } else {
            return updateHelper.getIntegerPureCallBackData();
        }
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        KeyboardBuilder builder = keyboardBuilder.getKeyboard();
        if (updateHelper.getUser().getToGoMeetings().stream().anyMatch(m -> m.getId().equals(meeting.getId()))) {
            builder.setRow("Unsubscribe", SingleMeetingViewUnsubscribeStage.class.getSimpleName() + meeting.getId());
        } else {
            Integer participants = meeting.getParticipants().size();
            Integer participantLimit = meeting.getMeetingInfo().getParticipantLimit();

            //TODO remove first statement and add participants limit to all meetings
            if ((participants == null || participants == 0)
                || (participantLimit != null
                        && participants != null
                        && participants < participantLimit)) {
                builder.setRow("Join", SingleMeetingViewJoinStage.class.getSimpleName() + meeting.getId());
            }
        }
        return builder
                .setRow(ImmutableMap.of(
                        "Contact", SingleMeetingViewContactStage.class.getSimpleName() + meeting.getId(),
                        "Participants List", SingleMeetingParticipantsListStage.class.getSimpleName() + meeting.getId(),
                        "Map", SingleMeetingViewMapStage.class.getSimpleName() + meeting.getId()))
                .build();
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains() || updateHelper.isMessageContains(stageInfo.getKeyword());
    }
}
