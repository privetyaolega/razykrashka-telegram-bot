package com.razykrashka.bot.stage.meeting.view.single;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.service.MeetingService;
import com.razykrashka.bot.exception.EntityWasNotFoundException;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.all.ActiveMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.ExpiredMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SingleMeetingViewStage extends MainStage {

    @Value("${razykrashka.bot.meeting.view-per-page}")
    Integer meetingsPerPage;
    @Autowired
    MeetingMessageUtils meetingMessageUtils;
    @Autowired
    Meeting meeting;
    @Autowired
    MeetingService meetingService;

    public SingleMeetingViewStage() {
        stageInfo = StageInfo.SINGLE_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        Integer id = getMeetingId();
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (!optionalMeeting.isPresent()) {
            messageManager.replyLastMessage(super.getFormatString("meetingNotFound", id));
            throw new EntityWasNotFoundException("Meeting was not found. ID: " + id);
        }
        meeting = optionalMeeting.get();
        String messageText = meetingMessageUtils.createSingleMeetingFullText(meeting);
        messageManager.sendSimpleTextMessage(messageText, this.getKeyboard());
    }

    private Integer getMeetingId() {
        if (!updateHelper.getMessageText().isEmpty()) {
            String messageText = updateHelper.getMessageText();
            String keyword = this.getStageInfo().getKeyword();
            String meetingIdString = messageText.replace(keyword, "");
            return Integer.valueOf(meetingIdString);
        } else {
            return updateHelper.getIntegerPureCallBackData();
        }
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        KeyboardBuilder builder = keyboardBuilder.getKeyboard();
        if (updateHelper.getUser().getToGoMeetings().stream().anyMatch(m -> m.getId().equals(meeting.getId()))) {
            builder.setRow("Unsubscribe " + Emoji.RED_CROSS,
                    SingleMeetingViewUnsubscribeStage.class.getSimpleName() + meeting.getId());
        } else {
            int participants = meeting.getParticipants().size();
            Integer participantLimit = meeting.getMeetingInfo().getParticipantLimit();
            if (participants < participantLimit) {
                builder.setRow("Join " + Emoji.ROCK_HAND,
                        SingleMeetingViewJoinStage.class.getSimpleName() + meeting.getId());
            }
        }
        builder.setRow(ImmutableMap.of(
                "Contact", SingleMeetingViewContactStage.class.getSimpleName() + meeting.getId(),
                "Participants List", SingleMeetingParticipantsListStage.class.getSimpleName() + meeting.getId(),
                "Map", SingleMeetingViewMapStage.class.getSimpleName() + meeting.getId()));

        return builder.setRow(getNavigationBackButton())
                .build();
    }

    private Pair<String, String> getNavigationBackButton() {
        //TODO: update to work with meeting.getCreationState().getCreationStatus()
        Pair<String, String> pair;
        List<Meeting> meetings;
        if (meeting.getMeetingDateTime().isAfter(LocalDateTime.now())) {
            meetings = meetingService.getAllCreationStatusDone();
        } else {
            meetings = meetingService.getAllExpired();
        }
        Integer indexOfMeeting = IntStream.range(0, meetings.size())
                .filter(i -> meetings.get(i).getId().equals(meeting.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Meeting is absent in appropriate list. ID:" + meeting.getId()));

        int pageNumToShow = (int) Math.ceil(indexOfMeeting / new Double(meetingsPerPage));
        pageNumToShow = (pageNumToShow == 0) ? 1 : pageNumToShow;

        if (meeting.getMeetingDateTime().isAfter(LocalDateTime.now())) {
            pair = Pair.of(Emoji.LEFT_FINGER + " Active meetings", ActiveMeetingsViewStage.class.getSimpleName() + pageNumToShow);
        } else {
            pair = Pair.of(Emoji.LEFT_FINGER + " Archived meetings", ExpiredMeetingsViewStage.class.getSimpleName() + pageNumToShow);
        }
        return pair;
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains()
                || updateHelper.isMessageContains(stageInfo.getKeyword());
    }
}
