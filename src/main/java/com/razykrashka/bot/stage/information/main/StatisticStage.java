package com.razykrashka.bot.stage.information.main;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticStage extends InformationMainStage {

    @Value("${razykrashka.group.id}")
    Long groupChatId;

    @Override
    public void processCallBackQuery() {
        Long doneMeetingsCount = meetingRepository.countByMeetingDateTimeBefore();
        long usersUsingBotCount = telegramUserRepository.count();

        String message = getFormatString("main", doneMeetingsCount, usersUsingBotCount, getMembersCount());
        messageManager.updateOrSendDependsOnLastMessageOwner(message, getKeyboardWithHighlightedButton("Statistics"));
    }

    private int getMembersCount() {
        try {
            return updateHelper
                    .getBot()
                    .execute(new GetChatMembersCount().setChatId(groupChatId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataEquals();
    }
}