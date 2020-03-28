package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptFormatMeetingCreationSBSStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormatMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    Class<? extends BaseMeetingCreationSBSStage> nextStageClass = AcceptFormatMeetingCreationSBSStage.class;

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        meeting.setFormat(MeetingFormatEnum.NA);
        meetingRepository.save(meeting);

        String meetingInfo = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.deleteLastBotMessageIfHasKeyboard()
                .sendSimpleTextMessage(meetingInfo + "Please, enter meeting format", getKeyboard());
        setActiveNextStage(nextStageClass);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        "Online", nextStageClass.getSimpleName() + MeetingFormatEnum.ONLINE,
                        "Offline", nextStageClass.getSimpleName() + MeetingFormatEnum.OFFLINE))
                .setRow("Back to time edit", TimeMeetingCreationSBSStage.class.getSimpleName() + "edit")
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataContains(this.getClass().getSimpleName() + "edit");
    }
}
