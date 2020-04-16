package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.FinalMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.ParticipantsMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
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
        // TODO: add questions parsing
        String topic = updateHelper.getMessageText();

        meeting = getMeetingInCreation();
        MeetingInfo mi = meeting.getMeetingInfo();
        mi.setTopic(topic);
        mi.setQuestions("");
        meetingInfoRepository.save(mi);
        meeting.setMeetingInfo(mi);
        meetingRepository.save(meeting);

        razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public void processCallBackQuery() {
        if (updateHelper.isCallBackDataEquals(ACCEPT_RANDOM_TOPIC_CBQ)) {
            razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).handleRequest();
            return;
        }

        meeting = getMeetingInCreation();
        MeetingInfo mi = meeting.getMeetingInfo();

        List<MeetingInfo> meetingInfoList = meetingInfoRepository.findAllByParticipantLimitEquals(0);
        MeetingInfo randomMeetingInfo;
        do {
            int randomNumber = new Random().nextInt(meetingInfoList.size());
            randomMeetingInfo = meetingInfoList.get(randomNumber);
        } while (randomMeetingInfo.getTopic().equals(mi.getTopic()));

        mi.setTopic(randomMeetingInfo.getTopic());
        mi.setQuestions(randomMeetingInfo.getQuestions());
        meetingInfoRepository.save(mi);
        meetingRepository.save(meeting);

        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        Emoji.RANDOM_CUBE + " Random Topic", RANDOM_TOPIC_CBQ,
                        "Accept Topic " + Emoji.OK_HAND, ACCEPT_RANDOM_TOPIC_CBQ))
                .setRow(getString("backButton"), ParticipantsMeetingCreationSBSStage.class.getSimpleName())
                .build();
        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
        messageManager.updateMessage(messageText, keyboardMarkup);
        super.setActiveNextStage(AcceptTopicMeetingCreationSBSStage.class);
    }

    @Override
    public boolean isStageActive() {
        return (super.isStageActive() && !updateHelper.isCallBackDataContains(ParticipantsMeetingCreationSBSStage.class.getSimpleName())
                || updateHelper.isCallBackDataContains());
    }
}