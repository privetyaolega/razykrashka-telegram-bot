package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.service.MeetingService;
import com.razykrashka.bot.service.config.property.meeting.MeetingProperties;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class PaginationMeetingsViewStage extends MainStage {

    @Autowired
    MeetingProperties meetingProperties;
    @Autowired
    MeetingMessageUtils meetingMessageUtils;
    @Autowired
    MeetingService meetingService;

    InlineKeyboardMarkup keyboard;
    List<Meeting> meetings;

    protected void generateMainMessage(Function<List<Meeting>, String> function) {
        keyboard = new InlineKeyboardMarkup();
        if (meetings.size() == 0) {
            messageManager.updateOrSendDependsOnLastMessageOwner(getString("noMeetings"), null);
        } else {
            int pageNumToShow = getPageNumToShow();
            int totalPagesAmount = (int) Math.ceil(meetings.size() / new Double(meetingProperties.getViewPerPage()));
            List<Meeting> meetingsToShow = getMeetingsSublistForCurrentPage(pageNumToShow, totalPagesAmount);

            String header = String.format(getString("header"), meetings.size());
            String meetingsString = function.apply(meetingsToShow);
            if (meetings.size() > meetingProperties.getViewPerPage()) {
                keyboard = keyboardBuilder.getPaginationKeyboard(this.getClass(), pageNumToShow, totalPagesAmount);
            }
            if (updateHelper.isUpdateFromGroup()) {
                String userChatId = String.valueOf(razykrashkaBot.getRealUpdate().getCallbackQuery().getFrom().getId());
                messageManager
                        .disableKeyboardLastBotMessage(userChatId)
                        .sendMessage(new SendMessage()
                                .setParseMode(ParseMode.HTML)
                                .setChatId(userChatId)
                                .setText(header + meetingsString)
                                .setReplyMarkup(keyboard)
                                .disableWebPagePreview());
            } else {
                messageManager.updateOrSendDependsOnLastMessageOwner(header + meetingsString, keyboard);
            }
        }
    }

    /**
     * By default, first page is shown
     * <p>
     * Presence of Call Back Query indicates that we need to display
     * not the first page, but page that goes from CBQ
     * CBQ Example: 'AllMeetingViewStage3' - need to display the third page
     */
    private int getPageNumToShow() {
        String className = this.getClass().getSimpleName();
        String pageNumber = updateHelper.getCallBackData().replace(className, "");
        if (!updateHelper.getCallBackData().equals(className) && !pageNumber.isEmpty()) {
            try {
                return Integer.parseInt(pageNumber);
            } catch (NumberFormatException n) {
                return 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    private List<Meeting> getMeetingsSublistForCurrentPage(int pageNumToShow, int totalPagesAmount) {
        int limit = meetingProperties.getViewPerPage();
        if (totalPagesAmount == pageNumToShow && totalPagesAmount * meetingProperties.getViewPerPage() != meetings.size()) {
            limit = meetings.size() % meetingProperties.getViewPerPage();
        }
        return meetings.stream()
                .skip((pageNumToShow - 1) * meetingProperties.getViewPerPage())
                .limit(limit)
                .collect(Collectors.toList());
    }
}