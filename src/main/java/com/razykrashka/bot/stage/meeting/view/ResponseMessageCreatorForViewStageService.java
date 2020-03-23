package com.razykrashka.bot.stage.meeting.view;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResponseMessageCreatorForViewStageService {

    @Autowired
    private MeetingMessageUtils meetingMessageUtils;
    @Autowired
    private RazykrashkaBot razykrashkaBot;
    @Autowired
    private UpdateHelper updateHelper;
    @Autowired
    private MessageManager messageManager;
    @Autowired
    private KeyboardBuilder keyboardBuilder;

    private static final Integer MEETINGS_PER_PAGE = 4;

    public void createResponse(List<Meeting> meetings, Class callerClass, Integer currentPageNumber) {
        if (meetings.size() == 0) {
            messageManager.sendSimpleTextMessage("NO MEETINGS :(");
        } else {
            Integer totalPagesAmount = (int) Math.ceil(meetings.size() / new Double(MEETINGS_PER_PAGE));

            List<Meeting> meetingsToShowOnCurrentPage = getMeetingsForCurrentPage(totalPagesAmount, currentPageNumber, meetings);
            String messageText = meetingMessageUtils.createMeetingsText(meetingsToShowOnCurrentPage, updateHelper.getUser().getTelegramId());

            InlineKeyboardMarkup paginationKeyboard = getPaginationKeyboard(meetings, keyboardBuilder, currentPageNumber, totalPagesAmount, callerClass);

            if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
                messageManager.updateMessage(messageText, paginationKeyboard);
            } else {
                messageManager.sendSimpleTextMessage(messageText, paginationKeyboard);
            }
        }
    }

    private List<Meeting> getMeetingsForCurrentPage(Integer totalPagesAmount, Integer currentPageNumber, List<Meeting> meetings) {
        int limit = MEETINGS_PER_PAGE;
        if (totalPagesAmount.equals(currentPageNumber) && totalPagesAmount * MEETINGS_PER_PAGE != meetings.size()) {
            limit = meetings.size() % MEETINGS_PER_PAGE;
        }
        return meetings.stream()
                .skip((currentPageNumber - 1) * MEETINGS_PER_PAGE)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private InlineKeyboardMarkup getPaginationKeyboard(List<Meeting> meetings, KeyboardBuilder keyboardBuilder, Integer currentPageNumber, Integer totalPagesAmount, Class callerClass) {
        InlineKeyboardMarkup keyboard = null;
        if (meetings.size() > MEETINGS_PER_PAGE) {
            keyboard = keyboardBuilder.getPaginationKeyboard(callerClass, currentPageNumber, totalPagesAmount);
        }
        return keyboard;
    }
}
