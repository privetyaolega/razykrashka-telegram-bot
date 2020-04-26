package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.TopicCatalogue;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.db.repo.MeetingCatalogRepository;
import com.razykrashka.bot.exception.IncorrectInputDataFormatException;
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
    final static String TOPIC_ID_REGEXP = "(?i)id[$:\\-., ;*=]*\\d*";

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        MeetingInfo mi = meeting.getMeetingInfo();
        if (isGettingFromCatalog()) {
            Integer id = updateHelper.getIntDataFromMessage();
            Optional<TopicCatalogue> meetingCatalog = meetingCatalogRepository.findById(id);
            meetingCatalog.orElseThrow(() -> {
                messageManager.replyLastMessage("Meeting info #" + id + " was not found.");
                updateHelper.getBot().getContext().getBean(TopicMeetingCreationSBSStage.class).start();
                return new NoSuchEntityException("Meeting info #" + id + " was not found.");
            });
            TopicCatalogue mc = meetingCatalog.get();
            mi.setTopic(mc.getTopic());
            mi.setQuestions(mc.getQuestions());
        } else {
            setMeetingInfoFromMessage();
        }
        meetingInfoRepository.save(mi);
        meeting.setMeetingInfo(mi);
        meetingRepository.save(meeting);

        messageManager.deleteLastMessage();

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
        TopicCatalogue randomMeetingInfo = getRandomMeetingInfo(mi);

        mi.setTopic(randomMeetingInfo.getTopic());
        mi.setQuestions(randomMeetingInfo.getQuestions());
        meetingInfoRepository.save(mi);
        meetingRepository.save(meeting);

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
        messageManager.updateMessage(messageText, getKeyboard());
        super.setActiveNextStage(AcceptTopicMeetingCreationSBSStage.class);
    }

    private TopicCatalogue getRandomMeetingInfo(MeetingInfo actualMi) {
        List<TopicCatalogue> topicCatalogueList = StreamSupport
                .stream(meetingCatalogRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        TopicCatalogue randomMeetingInfo;
        do {
            int randomNumber = new Random().nextInt(topicCatalogueList.size());
            randomMeetingInfo = topicCatalogueList.get(randomNumber);
        } while (randomMeetingInfo.getTopic().equals(actualMi.getTopic()));
        return randomMeetingInfo;
    }

    private boolean isTopicInfoValid(List<String> list) {
        return list.stream()
                .filter(s -> !s.isEmpty())
                .count() > 3;
    }

    private void setMeetingInfoFromMessage() {
        List<String> list = Arrays.asList(updateHelper.getMessageText().split("\n"));
        if (!isTopicInfoValid(list)) {
            messageManager
                    .disableKeyboardLastBotMessage()
                    .replyLastMessage("Ooopppsss..." +
                            "\nIt seems, that you created topic not quite correctly \uD83E\uDD74" +
                            "\nDesign your topic according to our rules [link].");
            razykrashkaBot.getContext().getBean(TopicMeetingCreationSBSStage.class).start();
            throw new IncorrectInputDataFormatException("Meeting info is designed incorrectly");
        }

        String questions = getQuestionsInDbFormat(list);
        meeting.getMeetingInfo().setTopic(list.get(0));
        meeting.getMeetingInfo().setQuestions(questions);
    }

    private String getQuestionsInDbFormat(List<String> q) {
        return q.stream()
                .skip(1)
                .filter(l -> !l.isEmpty())
                .map(l -> l.trim()
                        .replaceAll("^\\d*[.,)*-?!]{0,5}", "")
                        .trim())
                .collect(Collectors.joining(";"));
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        Emoji.RANDOM_CUBE + " Random Topic", RANDOM_TOPIC_CBQ,
                        "Accept Topic " + Emoji.OK_HAND, ACCEPT_TOPIC_CBQ)
                )
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