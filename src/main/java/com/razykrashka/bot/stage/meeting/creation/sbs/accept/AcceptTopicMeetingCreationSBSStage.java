package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingCatalog;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.db.repo.MeetingCatalogRepository;
import com.razykrashka.bot.exception.NoSuchEntityException;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.FinalMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.ParticipantsMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TopicMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptTopicMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Autowired
    MeetingCatalogRepository meetingCatalogRepository;
    final static String RANDOM_TOPIC_CBQ = AcceptTopicMeetingCreationSBSStage.class.getSimpleName() + "Random";
    final static String ACCEPT_TOPIC_CBQ = AcceptTopicMeetingCreationSBSStage.class.getSimpleName() + "Accept";
    final static String TOPIC_ID_REGEXP = "(?i)id[$:\\-., ;*=]*\\d{0,3}";

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        MeetingInfo mi = meeting.getMeetingInfo();
        if (isGettingFromCatalog()) {
            Integer id = updateHelper.getIntDataFromMessage();
            Optional<MeetingCatalog> meetingCatalog = meetingCatalogRepository.findById(id);
            meetingCatalog.orElseThrow(() -> {
                //TODO: Notification message if ID doesn't exist
                updateHelper.getBot().getContext().getBean(TopicMeetingCreationSBSStage.class).processCallBackQuery();
                return new NoSuchEntityException("Meeting info #" + id + " was not found.");
            });
            MeetingCatalog mc = meetingCatalog.get();
            mi.setTopic(mc.getTopic());
            mi.setQuestions(mc.getQuestions());
        } else {
            setMeetingInfoFromMessage();
        }
        meetingInfoRepository.save(mi);
        meeting.setMeetingInfo(mi);
        meetingRepository.save(meeting);

        razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).handleRequest();
    }

    private boolean isGettingFromCatalog() {
        return updateHelper
                .getMessageText()
                .matches(TOPIC_ID_REGEXP);
    }

    @Override
    public void processCallBackQuery() {
        if (updateHelper.isCallBackDataEquals(ACCEPT_TOPIC_CBQ)) {
            razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).handleRequest();
            return;
        }

        meeting = getMeetingInCreation();
        MeetingInfo mi = meeting.getMeetingInfo();
        MeetingCatalog randomMeetingInfo = getRandomMeetingInfo(mi);

        mi.setTopic(randomMeetingInfo.getTopic());
        mi.setQuestions(randomMeetingInfo.getQuestions());
        meetingInfoRepository.save(mi);
        meetingRepository.save(meeting);

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
        messageManager.updateMessage(messageText, getKeyboard());
        super.setActiveNextStage(AcceptTopicMeetingCreationSBSStage.class);
    }

    private MeetingCatalog getRandomMeetingInfo(MeetingInfo actualMi) {
        List<MeetingCatalog> meetingCatalogList = StreamSupport
                .stream(meetingCatalogRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        MeetingCatalog randomMeetingInfo;
        do {
            int randomNumber = new Random().nextInt(meetingCatalogList.size());
            randomMeetingInfo = meetingCatalogList.get(randomNumber);
        } while (randomMeetingInfo.getTopic().equals(actualMi.getTopic()));
        return randomMeetingInfo;
    }

    private void setMeetingInfoFromMessage() {
        List<String> list = Arrays.asList(updateHelper.getMessageText().split("\n"));
        if (list.size() == 1) {
            //TODO: Create validation for topic/questions input message
        }
        String questions = list.stream()
                .skip(1)
                .collect(Collectors.joining(";"));
        meeting.getMeetingInfo().setTopic(list.get(0));
        meeting.getMeetingInfo().setQuestions(questions);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        Emoji.RANDOM_CUBE + " Random Topic", RANDOM_TOPIC_CBQ,
                        "Accept Topic " + Emoji.OK_HAND, ACCEPT_TOPIC_CBQ))
                .setRow(getString("backButton"), ParticipantsMeetingCreationSBSStage.class.getSimpleName())
                .build();
    }

    @Override
    public boolean isStageActive() {
        return (super.isStageActive()
                && !updateHelper.isCallBackDataContains(ParticipantsMeetingCreationSBSStage.class.getSimpleName())
                || updateHelper.isCallBackDataContains());
    }
}