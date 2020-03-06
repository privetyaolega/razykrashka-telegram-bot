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

@Log4j2
@Component
public class AllMeetingViewStage extends MainStage {

	@Autowired
	private MeetingMessageUtils meetingMessageUtils;
	private Integer pageNumToShow = 1;
	private static final Integer MEETINGS_PER_PAGE = 5;
	private InlineKeyboardMarkup keyboard = null;

	public AllMeetingViewStage() {
		stageInfo = StageInfo.ALL_MEETING_VIEW;
	}

	@Override
	public void handleRequest() {
		List<Meeting> meetings = meetingRepository.findAllByCreationStatusEqualsAndTelegramUser(CreationStatus.DONE, razykrashkaBot.getUser());

		if (meetings.size() == 0) {
			messageManager.sendSimpleTextMessage("NO MEETINGS :(");
		} else {
			if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
				pageNumToShow = Integer.parseInt(getPureCallBackData());
			}
			int firstMeetingIndex = (pageNumToShow - 1) * MEETINGS_PER_PAGE;
			List<Meeting> meetingsToShow = meetings.subList(firstMeetingIndex, firstMeetingIndex + MEETINGS_PER_PAGE);
			String messageText = meetingMessageUtils.createMeetingsText(meetingsToShow);

			if (meetings.size() > MEETINGS_PER_PAGE) {
				keyboard = keyboardBuilder.getPaginationKeyboard(this.getClass(), pageNumToShow, meetings.size());
			}
			messageManager.sendSimpleTextMessage(messageText, keyboard);
		}
	}
}