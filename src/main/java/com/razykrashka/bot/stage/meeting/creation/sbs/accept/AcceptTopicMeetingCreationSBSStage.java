package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.FinalMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.ParticipantsMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Random;

@Log4j2
@Component
public class AcceptTopicMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    private final static String RANDOM_TOPIC_CBQ = AcceptTopicMeetingCreationSBSStage.class.getSimpleName() + "Random";
    private final static String ACCEPT_RANDOM_TOPIC_CBQ = AcceptTopicMeetingCreationSBSStage.class.getSimpleName() + "Accept";

    @Override
    public void handleRequest() {
        String topic = updateHelper.getMessageText();
        saveTopic(topic, "");
        razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public boolean processCallBackQuery() {
        if (updateHelper.isCallBackDataEquals(ACCEPT_RANDOM_TOPIC_CBQ)) {
            razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).handleRequest();
            return true;
        }

        List<MeetingInfo> meetingInfoList = meetingInfoRepository.findAllByParticipantLimitEquals(0);
        MeetingInfo meetingInfo = meetingInfoList.get(new Random().nextInt(meetingInfoList.size()));
        saveTopic(meetingInfo.getTopic(), meetingInfo.getQuestions());

        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        "Random Topic", RANDOM_TOPIC_CBQ,
                        "Accept Topic", ACCEPT_RANDOM_TOPIC_CBQ))
                .setRow(getString("backButton"), ParticipantsMeetingCreationSBSStage.class.getSimpleName())
                .build();
        String meetingInfoMessage = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.updateMessage(meetingInfoMessage + getString("input"), keyboardMarkup);
        super.setActiveNextStage(AcceptTopicMeetingCreationSBSStage.class);
        return true;
    }

    private void saveTopic(String topic, String questions) {
        meeting = getMeetingInCreation();
        MeetingInfo currentMeetingInfo = meeting.getMeetingInfo();
        currentMeetingInfo.setTopic(topic);
        currentMeetingInfo.setQuestions(" " + questions.replaceAll(" +", " "));
        meetingInfoRepository.save(currentMeetingInfo);
        meetingRepository.save(meeting);
    }

    @Override
    public boolean isStageActive() {
        return (super.isStageActive() && !updateHelper.isCallBackDataContains(ParticipantsMeetingCreationSBSStage.class.getSimpleName())
                || updateHelper.isCallBackDataContains());
    }
}