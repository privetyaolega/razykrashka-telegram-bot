package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptLevelMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;

@Log4j2
@Component
public class LevelMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        if (Optional.ofNullable(meeting.getMeetingInfo()).isPresent()) {
            meeting.getMeetingInfo().setSpeakingLevel(null);
            meetingInfoRepository.save(meeting.getMeetingInfo());
            meetingRepository.save(meeting);
        }

        messageManager.deleteLastBotMessageIfHasKeyboard();
        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of("Elementary", "ELEMENTARY",
                        "Pre-Intermediate", "PRE_INTERMEDIATE"))
                .setRow(ImmutableMap.of("Intermediate", "INTERMEDIATE",
                        "Upper-Intermediate", "UPPER_INTERMEDIATE"))
                .setRow(ImmutableMap.of("Advanced", "ADVANCED",
                        "Native", "NATIVE"))
                .setRow(super.getString("back"), LocationMeetingCreationSBSStage.class.getSimpleName())
                .build();

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.sendSimpleTextMessage(messageText + getString("input"), keyboardMarkup);
        setActiveNextStage(AcceptLevelMeetingCreationSBSStage.class);
    }

    @Override
    public boolean isStageActive() {
        return super.getStageActivity() || updateHelper.isCallBackDataContains();
    }
}