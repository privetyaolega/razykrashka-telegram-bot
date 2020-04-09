package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptFormatMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormatMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    Class<? extends BaseMeetingCreationSBSStage> nextStageClass = AcceptFormatMeetingCreationSBSStage.class;
    Class<? extends BaseMeetingCreationSBSStage> previousStageClass = TimeMeetingCreationSBSStage.class;

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        meeting.setFormat(MeetingFormatEnum.NA);
        meetingRepository.save(meeting);

        String message = meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
        messageManager.deleteLastBotMessageIfHasKeyboard()
                .sendSimpleTextMessage(message, getKeyboard());
        setActiveNextStage(nextStageClass);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        Emoji.INTERNET + " Online", nextStageClass.getSimpleName() + MeetingFormatEnum.ONLINE,
                        "Offline " + Emoji.COFFEE, nextStageClass.getSimpleName() + MeetingFormatEnum.OFFLINE))
                .setRow(getString("backButton"), previousStageClass.getSimpleName() + "edit")
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataContains(this.getClass().getSimpleName() + "edit");
    }
}
