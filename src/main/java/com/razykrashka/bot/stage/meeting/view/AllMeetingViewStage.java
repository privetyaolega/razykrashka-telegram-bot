package com.razykrashka.bot.stage.meeting.view;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class AllMeetingViewStage extends MainStage {

    private static final Integer MEETINGS_PER_PAGE = 4;
    @Autowired
    private MeetingMessageUtils meetingMessageUtils;
    private InlineKeyboardMarkup keyboard;
    private List<Meeting> meetings;
    private Integer pageNumToShow;
    private Integer totalPagesAmount;

    public AllMeetingViewStage() {
        stageInfo = StageInfo.ALL_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        meetings = meetingRepository.findAllByCreationStatus(CreationStatus.DONE);

        if (meetings.size() == 0) {
            messageManager.sendSimpleTextMessage("NO MEETINGS :(");
        } else {
            initPageNumToShow();
            totalPagesAmount = (int) Math.ceil(meetings.size() / new Double(MEETINGS_PER_PAGE));
            List<Meeting> meetingsToShow = getMeetingsSublistForCurrentPage();

            String messageText = meetingMessageUtils.createMeetingsText(meetingsToShow, meetings.size());
            if (meetings.size() > MEETINGS_PER_PAGE) {
                keyboard = keyboardBuilder.getPaginationKeyboard(this.getClass(), pageNumToShow, totalPagesAmount);
            }
            if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
                messageManager.updateMessage(messageText, keyboard);
            } else {
                messageManager.sendSimpleTextMessage(messageText, keyboard);
            }
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
    private void initPageNumToShow() {
        pageNumToShow = razykrashkaBot.getRealUpdate().hasCallbackQuery() ? updateHelper.getIntegerPureCallBackData() : 1;
    }

    private List<Meeting> getMeetingsSublistForCurrentPage() {
        int limit = MEETINGS_PER_PAGE;
        if (totalPagesAmount.equals(pageNumToShow) && totalPagesAmount * MEETINGS_PER_PAGE != meetings.size()) {
            limit = meetings.size() % MEETINGS_PER_PAGE;
        }
        return meetings.stream()
                .skip((pageNumToShow - 1) * MEETINGS_PER_PAGE)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains() || updateHelper.isMessageTextEquals(this.getStageInfo().getKeyword());
    }
}