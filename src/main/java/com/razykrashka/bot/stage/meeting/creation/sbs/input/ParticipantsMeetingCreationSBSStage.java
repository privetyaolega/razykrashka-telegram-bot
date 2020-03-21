package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptParticipantsMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;

@Log4j2
@Component
public class ParticipantsMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        if (Optional.ofNullable(meeting.getMeetingInfo()).isPresent()) {
            meeting.getMeetingInfo().setParticipantLimit(null);
            meeting.getMeetingInfo().setTopic(null);
            meeting.getMeetingInfo().setQuestions(null);
            meetingInfoRepository.save(meeting.getMeetingInfo());
            meetingRepository.save(meeting);
        }

        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        "2", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "2",
                        "3", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "3"))
                .setRow(ImmutableMap.of(
                        "4", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "5",
                        "5", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "6"))
                .setRow(ImmutableMap.of(
                        "6", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "6",
                        "7", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "7",
                        "8", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "8"))
                .setRow(ImmutableMap.of(
                        "9", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "9",
                        "10", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "10",
                        ">10", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "100"))
                .setRow(getString("back"), LevelMeetingCreationSBSStage.class.getSimpleName())
                .build();

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.deleteLastBotMessageIfHasKeyboard()
                .sendSimpleTextMessage(messageText + getString("input"), keyboardMarkup);
        super.setActiveNextStage(AcceptParticipantsMeetingCreationSBSStage.class);
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive() || updateHelper.isCallBackDataEquals();
    }
}