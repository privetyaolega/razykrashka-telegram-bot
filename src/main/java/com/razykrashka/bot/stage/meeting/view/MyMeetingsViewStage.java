package com.razykrashka.bot.stage.meeting.view;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
public class MyMeetingsViewStage extends MainStage {

	@Autowired
	private MeetingMessageUtils meetingMessageUtils;

	public MyMeetingsViewStage() {
		stageInfo = StageInfo.MY_MEETING_VIEW;
	}

	@Override
	public List<String> getValidKeywords() {
		return null;
	}

	@Override
	public void handleRequest() {
		List<Meeting> userMeetings = meetingRepository.findAllByTelegramUser(razykrashkaBot.getUser());

		if (userMeetings == null) {
			messageManager.sendSimpleTextMessage("NO MEETINGS :(");
		} else {
			String messageText = userMeetings.stream().skip(0).limit(20)
					.map(meeting -> isMyMeeting(meeting, razykrashkaBot.getUser().getTelegramId()) + meetingMessageUtils.createSingleMeetingMainInformationText(meeting))
					.collect(Collectors.joining(getStringMap().get("delimiterLine"),
							"\uD83D\uDCAB Найдено " + userMeetings.size() + " встреч(и)\n\n", ""));
			if (userMeetings.size() > 5) {
				//TODO: PAGINATION INLINE KEYBOARD
			}
			messageManager.sendSimpleTextMessage(messageText);
		}
	}

	private String isMyMeeting(Meeting meeting, Integer telegramId) {
		if (meeting.getTelegramUser().getTelegramId().equals(telegramId)) {
			return "**** Created By Me ****** \n";
		} else {
			return "";
		}
	}

	@Override
	public boolean isStageActive() {
		Message message = razykrashkaBot.getRealUpdate().getMessage();
		if (message == null) {
			return false;
		} else {
			return message.getText().equals("View My Meetings");
		}
	}
}
