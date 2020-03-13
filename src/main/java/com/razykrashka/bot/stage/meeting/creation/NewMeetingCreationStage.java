package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.db.entity.razykrashka.meeting.SpeakingLevel;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.ui.helpers.MapLocationHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class NewMeetingCreationStage extends MainStage {

	@Autowired
	MapLocationHelper mapLocationHelper;

	private String message;
	private Meeting meetingModel;

	public NewMeetingCreationStage() {
		stageInfo = StageInfo.NEW_MEETING_CREATION;
	}

	@Override
	public void handleRequest() {
		message = updateHelper.getMessageText().replace("@Test7313494Bot", "").trim();
		try {
			Map<String, String> meetingMap = Arrays.stream(message.split("\\n\\n")).skip(1)
					.map(x -> x.replace("\n", ""))
					.collect(Collectors.toMap((line) -> line.split(":")[0].trim(), (x) -> x.split(":")[1].trim()));

			MeetingInfo meetingInfo = MeetingInfo.builder()
					.questions(meetingMap.get("QUESTIONS"))
					.topic(meetingMap.get("TOPIC"))
					.speakingLevel(SpeakingLevel.ADVANCED)
					.build();
			meetingInfoRepository.save(meetingInfo);

			Location location = mapLocationHelper.getLocation(meetingMap.get("LOCATION"));
			locationRepository.save(location);

			meetingModel = Meeting.builder()
					.telegramUser(razykrashkaBot.getUser())
					.meetingDateTime(LocalDateTime.parse(meetingMap.get("DATE").trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm")))
					.creationDateTime(LocalDateTime.now())
					.startCreationDateTime(LocalDateTime.now())
					.meetingInfo(meetingInfo)
					.location(location)
					.creationStatus(CreationStatus.DONE)
					.build();
			meetingRepository.save(meetingModel);

			razykrashkaBot.getUser().setPhoneNumber(meetingMap.get("CONTACT NUMBER"));
			razykrashkaBot.getUser().getToGoMeetings().add(meetingModel);
			razykrashkaBot.getUser().getCreatedMeetings().add(meetingModel);
			telegramUserRepository.save(razykrashkaBot.getUser());

			messageManager.sendSimpleTextMessage("MEETING CREATED")
					.sendSticker("successMeetingCreationSticker");
		} catch (Exception e) {
			e.printStackTrace();
			messageManager.sendSimpleTextMessage("SOMETHING WENT WROND DURING MEETING CREATION")
					.sendSticker("failSticker.png");
			razykrashkaBot.getContext().getBean(CreateMeetingByTemplateStage.class).handleRequest();
		}
	}

	@Override
	public boolean isStageActive() {
		return updateHelper.isMessageContains(this.getStageInfo().getKeyword());
	}
}
