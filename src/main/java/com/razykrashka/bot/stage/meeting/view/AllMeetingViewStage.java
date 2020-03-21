package com.razykrashka.bot.stage.meeting.view;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllMeetingViewStage extends MainStage {

    @Value("${razykrashka.bot.meeting.view-per-page}")
    Integer meetingsPerPage;
    @Autowired
    MeetingMessageUtils meetingMessageUtils;
    InlineKeyboardMarkup keyboard;
    List<Meeting> meetings;
    Integer pageNumToShow;
    Integer totalPagesAmount;

    public AllMeetingViewStage() {
        stageInfo = StageInfo.ALL_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        meetings = meetingRepository.findAllByStatusEqualsDone();

        if (meetings.size() == 0) {
            messageManager.sendSimpleTextMessage(getString("noMeetings"));
        } else {
            initPageNumToShow();
            List<Meeting> meetingsToShow = getMeetingsSublistForCurrentPage();

            String messageText = meetingMessageUtils.createMeetingsText(meetingsToShow, meetings.size());
            if (meetings.size() > meetingsPerPage) {
                keyboard = keyboardBuilder.getPaginationKeyboard(this.getClass(), pageNumToShow, totalPagesAmount);
            }
            messageManager.updateOrSendDependsOnLastMessageOwner(messageText, keyboard);
        }
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    /**
     * By default, first page is shown
     * <p>
     * Presence of Call Back Query indicates that we need to display
     * not the first page, but page that goes from CBQ
     * CBQ Example: 'AllMeetingViewStage3' - need to display the third page
     */
    void initPageNumToShow() {
        pageNumToShow = razykrashkaBot.getRealUpdate().hasCallbackQuery() ? updateHelper.getIntegerPureCallBackData() : 1;
        totalPagesAmount = (int) Math.ceil(meetings.size() / new Double(meetingsPerPage));
    }

    List<Meeting> getMeetingsSublistForCurrentPage() {
        int limit = meetingsPerPage;
        if (totalPagesAmount.equals(pageNumToShow) && totalPagesAmount * meetingsPerPage != meetings.size()) {
            limit = meetings.size() % meetingsPerPage;
        }
        return meetings.stream()
                .skip((pageNumToShow - 1) * meetingsPerPage)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains() || updateHelper.isMessageTextEquals(this.getStageInfo().getKeyword());
    }
}