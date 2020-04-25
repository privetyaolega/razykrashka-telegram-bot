package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptParticipantsMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Optional;

@Log4j2
@Component
public class ParticipantsMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    Class<? extends BaseMeetingCreationSBSStage> nextStageClass = AcceptParticipantsMeetingCreationSBSStage.class;

    @Override
    public void processCallBackQuery() {
        messageManager.updateMessage(getMeetingMessage(), getKeyboard());
        super.setActiveNextStage(nextStageClass);
    }

    @Override
    public void handleRequest() {
        messageManager
                .disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(getMeetingMessage(), getKeyboard());
        super.setActiveNextStage(nextStageClass);
    }

    private String getMeetingMessage() {
        meeting = getMeetingInCreation();
        if (Optional.ofNullable(meeting.getMeetingInfo()).isPresent()) {
            meeting.getMeetingInfo().setParticipantLimit(null);
            meeting.getMeetingInfo().setTopic(null);
            meeting.getMeetingInfo().setQuestions(null);
            meetingInfoRepository.save(meeting.getMeetingInfo());
            meetingRepository.save(meeting);
        }
        return meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        "2", nextStageClass.getSimpleName() + "2",
                        "3", nextStageClass.getSimpleName() + "3"))
                .setRow(ImmutableMap.of(
                        "4", nextStageClass.getSimpleName() + "4",
                        "5", nextStageClass.getSimpleName() + "5"))
                .setRow(ImmutableMap.of(
                        "6", nextStageClass.getSimpleName() + "6",
                        "7", nextStageClass.getSimpleName() + "7",
                        "8+", nextStageClass.getSimpleName() + "25"))
                .setRow(getString("back"), LevelMeetingCreationSBSStage.class.getSimpleName())
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataEquals();
    }
}