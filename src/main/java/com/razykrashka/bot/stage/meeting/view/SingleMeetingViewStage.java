package com.razykrashka.bot.stage.meeting.view;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class SingleMeetingViewStage extends MainStage {

	@Autowired
	private MeetingMessageUtils meetingMessageUtils;
	private Meeting meeting;

	public SingleMeetingViewStage() {
		stageInfo = StageInfo.SINGLE_MEETING_VIEW;
	}

	@Override
	public void handleRequest() {
		Integer id = Integer.valueOf(razykrashkaBot.getUpdate().getMessage().getText()
				.replace(this.getStageInfo().getKeyword(), ""));
		meeting = meetingRepository.findById(id).get();

		String messageText = meetingMessageUtils.createSingleMeetingFullText(meeting);
		messageManager.sendSimpleTextMessage(messageText, this.getKeyboard());
	}

	@Override
	public ReplyKeyboard getKeyboard() {
		KeyboardBuilder builder = keyboardBuilder.getKeyboard();
		if (razykrashkaBot.getUser().getToGoMeetings().stream().anyMatch(m -> m.getId().equals(meeting.getId()))) {
			builder.setRow("Unsubscribe", stageInfo.getStageName() + "_unsubscribe" + meeting.getId());
		} else {
			builder.setRow("Join", stageInfo.getStageName() + "_join" + meeting.getId());
		}
		return builder
				.setRow(ImmutableMap.of(
						"Contact", stageInfo.getStageName() + "_contact" + meeting.getId(),
						"Map", stageInfo.getStageName() + "_map" + meeting.getId()))
				.build();
	}

	@Override
	public boolean processCallBackQuery() {
		String callBackData = razykrashkaBot.getCallbackQuery().getData();

		meeting = StreamSupport.stream(meetingRepository.findAll().spliterator(), false).
				filter(x -> callBackData.contains(String.valueOf(x.getId()))).findFirst().get();

		if (callBackData.equals(stageInfo.getStageName() + "_contact" + meeting.getId())) {
			razykrashkaBot.sendContact(new SendContact().setLastName(meeting.getTelegramUser().getLastName())
					.setFirstName(meeting.getTelegramUser().getFirstName())
					.setPhoneNumber(meeting.getTelegramUser().getPhoneNumber()));
		}
		if (callBackData.equals(stageInfo.getStageName() + "_map" + meeting.getId())) {
			razykrashkaBot.sendVenue(new SendVenue()
					.setTitle(meeting.getMeetingDateTime().format(DateTimeFormatter
							.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH)))
					.setLatitude(meeting.getLocation().getLatitude())
					.setLongitude(meeting.getLocation().getLongitude())
					.setAddress(meeting.getLocation().getAddress()));
		}

		if (callBackData.equals(stageInfo.getStageName() + "_join" + meeting.getId())) {
			razykrashkaBot.getUser().addMeetingTotoGoMeetings(meeting);
			telegramUserRepository.save(razykrashkaBot.getUser());
			messageManager.sendSimpleTextMessage("yyyyyyyyyyyyyyyyyeah");
		}

		if (callBackData.equals(stageInfo.getStageName() + "_unsubscribe" + meeting.getId())) {
			//meetingRepository.deleteMeeting(meeting.getId());
			razykrashkaBot.getUser().removeFromToGoMeetings(meeting);
			telegramUserRepository.save(razykrashkaBot.getUser());
			messageManager.sendSimpleTextMessage(":(");
		}
		return true;
	}

	@Override
	public boolean isStageActive() {
		return updateHelper.isMessageContains(stageInfo.getKeyword());
	}
}
