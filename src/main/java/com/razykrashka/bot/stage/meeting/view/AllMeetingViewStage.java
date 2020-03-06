package com.razykrashka.bot.stage.meeting.view;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class AllMeetingViewStage extends MainStage {

	@Autowired
	private MeetingMessageUtils meetingMessageUtils;
	private Integer pageNumToShow = 1;
	private static final Integer PAGE_MEETINGS_AMOUNT = 5;

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
			String messageText = meetings.stream()
					.skip(pageNumToShow * PAGE_MEETINGS_AMOUNT)
					.limit(PAGE_MEETINGS_AMOUNT)
					.map(meeting -> meetingMessageUtils.createSingleMeetingMainInformationText(meeting))
					.collect(Collectors.joining(getStringMap().get("delimiterLine"),
							String.format(getStringMap().get("delimiterLine"), meetings.size()), ""));
			if (meetings.size() > 5) {
				InlineKeyboardMarkup keyboard = keyboardBuilder.getPaginationKeyboard(this.getClass(),
						pageNumToShow, meetings.size());
				messageManager.sendSimpleTextMessage(messageText, keyboard);
			}
			messageManager.sendSimpleTextMessage(messageText);
		}
	}

	@Override
	public boolean isStageActive() {
		Message message = razykrashkaBot.getRealUpdate().getMessage();
		if (message == null) {
			return false;
		} else {
			return message.getText().equals("View Meetings");
		}
	}
}