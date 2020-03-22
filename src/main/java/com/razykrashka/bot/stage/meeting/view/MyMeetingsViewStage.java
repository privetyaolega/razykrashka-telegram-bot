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

	@Autowired
	private ResponseMessageCreatorForViewStageService responseMessageCreatorForViewStageService;

	public MyMeetingsViewStage() {
		stageInfo = StageInfo.MY_MEETING_VIEW;
	}

	@Override
	public void handleRequest() {
		List<Meeting> meetings = meetingRepository.findAllScheduledMeetingsForUserById(razykrashkaBot.getUser().getId());
		Integer currentPageNumber = razykrashkaBot.getRealUpdate().hasCallbackQuery() ? updateHelper.getIntegerPureCallBackData() : 1;
		responseMessageCreatorForViewStageService.createResponse(meetings, this.getClass(), currentPageNumber);
	}

	@Override
	public boolean processCallBackQuery() {
		handleRequest();
		return true;
	}

	@Override
	public boolean isStageActive() {
		return updateHelper.isCallBackDataContains() || updateHelper.isMessageTextEquals(this.getStageInfo().getKeyword());
	}
}