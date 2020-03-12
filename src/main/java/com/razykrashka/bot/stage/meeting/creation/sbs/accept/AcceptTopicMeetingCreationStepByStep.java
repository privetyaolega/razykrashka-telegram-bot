package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
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
public class AcceptTopicMeetingCreationStepByStep extends BaseMeetingCreationSBSStage {

    private final static String RANDOM_TOPIC_CBQ = AcceptTopicMeetingCreationStepByStep.class.getSimpleName() + "Random";
    private final static String ACCEPT_RANDOM_TOPIC_CBQ = AcceptTopicMeetingCreationStepByStep.class.getSimpleName() + "Accept";

    @Override
    public void handleRequest() {
        String topic = razykrashkaBot.getRealUpdate().getMessage().getText();
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
                .setRow("BACK TO PARTICIPANT LIMIT EDIT", ParticipantsMeetingCreationSBSStage.class.getSimpleName())
                .build();
        String meetingInfoMessage = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.updateMessage(meetingInfoMessage +
                "Please, input topic or generate random one.", keyboardMarkup);
        super.setActiveNextStage(AcceptTopicMeetingCreationStepByStep.class);
        return true;
    }

    private void saveTopic(String topic, String questions) {
        meeting = getMeetingInCreation();
        MeetingInfo currentMeetingInfo = meeting.getMeetingInfo();
        currentMeetingInfo.setTopic(topic);
        currentMeetingInfo.setQuestions(questions);
        meetingInfoRepository.save(currentMeetingInfo);
        meetingRepository.save(meeting);
    }

    @Override
    public boolean isStageActive() {
        boolean isEdit = false;
        if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
            isEdit = razykrashkaBot.getRealUpdate().getCallbackQuery()
                    .getData().contains(ParticipantsMeetingCreationSBSStage.class.getSimpleName());
        }
        return super.isStageActive() && !isEdit;
    }
}