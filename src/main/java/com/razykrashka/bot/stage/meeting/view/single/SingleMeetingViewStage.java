package com.razykrashka.bot.stage.meeting.view.single;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.stream.StreamSupport;

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
        Integer id = Integer.valueOf(updateHelper.getMessageText()
                .replace(this.getStageInfo().getKeyword(), ""));
        meeting = meetingRepository.findById(id).get();

        String messageText = meetingMessageUtils.createSingleMeetingFullText(meeting);
        messageManager.sendSimpleTextMessage(messageText, this.getKeyboard());
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        KeyboardBuilder builder = keyboardBuilder.getKeyboard();
        if (updateHelper.getUser().getToGoMeetings().stream().anyMatch(m -> m.getId().equals(meeting.getId()))) {
            builder.setRow("Unsubscribe", stageInfo.getStageName() + "_unsubscribe" + meeting.getId());
        } else {
            builder.setRow("Join", stageInfo.getStageName() + "_join" + meeting.getId());
        }
        return builder
                .setRow(ImmutableMap.of(
                        "Contact", SingleMeetingViewContactStage.class.getSimpleName() + meeting.getId(),
                        "Map", SingleMeetingViewMapStage.class.getSimpleName() + meeting.getId()))
                .build();
    }

    @Override
    public boolean processCallBackQuery() {
        String callBackData = updateHelper.getCallBackData();

        meeting = StreamSupport.stream(meetingRepository.findAll().spliterator(), false).
                filter(x -> callBackData.contains(String.valueOf(x.getId()))).findFirst().get();

        if (callBackData.equals(stageInfo.getStageName() + "_join" + meeting.getId())) {
            updateHelper.getUser().addMeetingTotoGoMeetings(meeting);
            telegramUserRepository.save(updateHelper.getUser());
            messageManager.sendSimpleTextMessage("yyyyyyyyyyyyyyyyyeah");
        }

        if (callBackData.equals(stageInfo.getStageName() + "_unsubscribe" + meeting.getId())) {
            //meetingRepository.deleteMeeting(meeting.getId());
            updateHelper.getUser().removeFromToGoMeetings(meeting);
            telegramUserRepository.save(updateHelper.getUser());
            messageManager.sendSimpleTextMessage(":(");
        }
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageContains(stageInfo.getKeyword());
    }
}