package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptParticipantsPMeetingCreationSBSStage;
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

        messageManager.deleteLastBotMessageIfHasKeyboard();
        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        "2️⃣", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "2",
                        "3️⃣", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "3",
                        "4️⃣", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "4"))
                .setRow(ImmutableMap.of(
                        "5️⃣", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "5",
                        "6️⃣", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "6",
                        "7️⃣", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "7"))
                .setRow(ImmutableMap.of(
                        "8️⃣", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "8",
                        "9️⃣", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "9",
                        "\uD83D\uDD1F", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "10"))
                .setRow(getString("back"), LevelMeetingCreationSBSStage.class.getSimpleName())
                .build();

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.sendSimpleTextMessage(messageText + getString("input"), keyboardMarkup);
        super.setActiveNextStage(AcceptParticipantsPMeetingCreationSBSStage.class);
    }

    @Override
    public boolean isStageActive() {
        boolean isQueryContainsClass = false;
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            isQueryContainsClass = razykrashkaBot.getRealUpdate().getCallbackQuery()
                    .getData().contains(this.getClass().getSimpleName());
        }
        return super.isStageActive() || isQueryContainsClass;
    }
}