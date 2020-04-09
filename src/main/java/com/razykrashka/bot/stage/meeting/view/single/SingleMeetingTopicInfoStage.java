package com.razykrashka.bot.stage.meeting.view.single;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.edit.delete.DeleteConfirmationSingleMeetingStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class SingleMeetingTopicInfoStage extends MainStage {

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
            int participants = meeting.getParticipants().size();
            Integer participantLimit = meeting.getMeetingInfo().getParticipantLimit();
            if (participants < participantLimit) {
                builder.setRow("Join " + Emoji.ROCK_HAND, SingleMeetingViewJoinStage.class.getSimpleName() + meeting.getId());
            }
        }

        if (updateHelper.getUser().equals(meeting.getTelegramUser())
                && meeting.getParticipants().contains(updateHelper.getUser())) {
            builder.setRow(Pair.of("Delete " + Emoji.RED_CROSS,
                    DeleteConfirmationSingleMeetingStage.class.getSimpleName() + meeting.getId()));
        }

        List<Pair<String, String>> buttonList = new ArrayList<>();
        buttonList.add(Pair.of(Emoji.ONE_PERSON_SILHOUETTE, SingleMeetingViewContactStage.class.getSimpleName() + meeting.getId()));
        buttonList.add(Pair.of(Emoji.FOLDER, SingleMeetingViewStage.class.getSimpleName() + meeting.getId()));

        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            buttonList.add(Pair.of(Emoji.LOCATION, SingleMeetingViewMapStage.class.getSimpleName() + meeting.getId()));
        }

        return builder.setRow(buttonList).build();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}