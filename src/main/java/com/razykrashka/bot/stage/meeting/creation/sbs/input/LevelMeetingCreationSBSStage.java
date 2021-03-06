package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptLevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Optional;

@Log4j2
@Component
public class LevelMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        messageManager
                .deleteLastBotMessageIfHasKeyboard()
                .sendSimpleTextMessage(getMeetingMessage(), getKeyboard());
        setActiveNextStage(AcceptLevelMeetingCreationSBSStage.class);
    }

    @Override
    public void processCallBackQuery() {
        messageManager.updateMessage(getMeetingMessage(), getKeyboard());
        setActiveNextStage(AcceptLevelMeetingCreationSBSStage.class);
    }

    private String getMeetingMessage() {
        meeting = getMeetingInCreation();
        if (Optional.ofNullable(meeting.getMeetingInfo()).isPresent()) {
            meeting.getMeetingInfo().setSpeakingLevel(null);
            meetingInfoRepository.save(meeting.getMeetingInfo());
            meetingRepository.save(meeting);
        }

        return meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of("Elementary", "ELEMENTARY",
                        "Pre-Intermediate", "PRE_INTERMEDIATE"))
                .setRow(ImmutableMap.of("Intermediate", "INTERMEDIATE",
                        "Upper-Intermediate", "UPPER_INTERMEDIATE"))
                .setRow(ImmutableMap.of("Advanced", "ADVANCED",
                        "Native", "NATIVE"))
                .setRow(getBackButton(meeting))
                .build();
    }

    private Pair<String, String> getBackButton(Meeting meeting) {
        Class<? extends BaseMeetingCreationSBSStage> editedClass;
        String buttonLabel;
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            editedClass = OfflineMeetingCreationSBSStage.class;
            buttonLabel = "Back to editing Location";
        } else {
            editedClass = FormatMeetingCreationSBSStage.class;
            buttonLabel = "Back to editing Meeting Format";
        }
        return Pair.of(Emoji.LEFT_FINGER + " " + buttonLabel, editedClass.getSimpleName() + "edit");
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataContains();
    }
}