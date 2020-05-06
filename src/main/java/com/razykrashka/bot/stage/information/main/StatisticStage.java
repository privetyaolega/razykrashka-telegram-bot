package com.razykrashka.bot.stage.information.main;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticStage extends InformationMainStage {

    public StatisticStage() {
        buttonLabel = "Statistics";
    }

    @Override
    public void processCallBackQuery() {
        long doneMeetingsCount = meetingRepository.countByMeetingDateTimeBefore();
        long usersUsingBotCount = telegramUserRepository.count();
        long membersCount = messageManager.getGroupMembersCount();
        String message = getFormatString("main", doneMeetingsCount, usersUsingBotCount, membersCount);
        messageManager.updateOrSendDependsOnLastMessageOwner(message, getKeyboardWithHighlightedButton());
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataEquals();
    }
}