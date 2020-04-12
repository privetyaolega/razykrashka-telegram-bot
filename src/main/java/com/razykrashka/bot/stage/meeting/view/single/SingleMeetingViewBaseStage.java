package com.razykrashka.bot.stage.meeting.view.single;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.edit.delete.DeleteConfirmationSingleMeetingStage;
import com.razykrashka.bot.stage.meeting.view.single.action.SingleMeetingViewJoinStage;
import com.razykrashka.bot.stage.meeting.view.single.action.SingleMeetingViewLeaveStage;
import com.razykrashka.bot.stage.meeting.view.single.info.SingleMeetingViewContactStage;
import com.razykrashka.bot.stage.meeting.view.single.info.SingleMeetingViewMapStage;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class SingleMeetingViewBaseStage extends MainStage {

    Meeting meeting;
    @Autowired
    MeetingMessageUtils meetingMessageUtils;
    final String joinStage = SingleMeetingViewJoinStage.class.getSimpleName();
    final String leaveStage = SingleMeetingViewLeaveStage.class.getSimpleName();
    final String contactStage = SingleMeetingViewContactStage.class.getSimpleName();
    final String deleteStage = DeleteConfirmationSingleMeetingStage.class.getSimpleName();
    final String topicStage = SingleMeetingTopicInfoStage.class.getSimpleName();
    final String mapStage = SingleMeetingViewMapStage.class.getSimpleName();
    final String mainStage = SingleMeetingViewMainStage.class.getSimpleName();

    protected Integer getMeetingId() {
        if (!updateHelper.getMessageText().isEmpty()) {
            String messageText = updateHelper.getMessageText();
            String keyword = this.getStageInfo().getKeyword();
            String meetingIdString = messageText.replace(keyword, "");
            return Integer.valueOf(meetingIdString);
        } else {
            return updateHelper.getIntDataFromCallBackQuery();
        }
    }

    protected boolean isUserParticipant() {
        return meeting.getParticipants().contains(updateHelper.getUser());
    }

    protected boolean isUserOwner() {
        return updateHelper.getUser().equals(meeting.getTelegramUser()) && isUserParticipant();
    }

    protected boolean isMeetingAboutStarting() {
        return meeting.getMeetingDateTime().plusHours(1).isAfter(LocalDateTime.now());
    }

    protected boolean hasFreePlaces() {
        int participants = meeting.getParticipants().size();
        Integer participantLimit = meeting.getMeetingInfo().getParticipantLimit();
        return participants < participantLimit;
    }

    protected Pair<String, String> getActionButton() {
        Pair<String, String> button = null;
        if (isUserParticipant() && isMeetingAboutStarting()) {
            button = Pair.of("Leave " + Emoji.DISAPPOINTED_RELIEVED, leaveStage + meeting.getId());
        } else if (hasFreePlaces()) {
            button = Pair.of("Join " + Emoji.ROCK_HAND, joinStage + meeting.getId());
        }
        return button;
    }

    protected Pair<String, String> getDeleteButton() {
        Pair<String, String> button = null;
        if (isUserOwner() && isMeetingAboutStarting()) {
            button = Pair.of("Delete " + Emoji.RED_CROSS, deleteStage + meeting.getId());
        }
        return button;
    }

    protected abstract List<Pair<String, String>> getMeetingInfoButtons();

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder
                .getKeyboard()
                .setRow(getActionButton())
                .setRow(getDeleteButton())
                .setRow(getMeetingInfoButtons())
                .build();
    }

    /*private Pair<String, String> getNavigationBackButton() {
        @Value("${razykrashka.bot.meeting.view-per-page}")
        Integer meetingsPerPage;
        //TODO: update to work with meeting.getCreationState().getCreationStatus()
        Pair<String, String> pair;
        List<Meeting> meetings;
        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            meetings = meetingService.getAllActiveOffline();
        } else {
            meetings = meetingService.getAllActiveOnline();
        }
        Integer indexOfMeeting = IntStream.range(0, meetings.size())
                .filter(i -> meetings.get(i).getId().equals(meeting.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Meeting is absent in appropriate list. ID:" + meeting.getId()));

        int pageNumToShow = (int) Math.ceil(indexOfMeeting / new Double(meetingsPerPage));
        pageNumToShow = (pageNumToShow == 0) ? 1 : pageNumToShow;

        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            pair = Pair.of(Emoji.LEFT_FINGER + " Offline meetings", OfflineMeetingsViewStage.class.getSimpleName() + pageNumToShow);
        } else {
            pair = Pair.of(Emoji.LEFT_FINGER + " Online meetings", OnlineMeetingsViewStage.class.getSimpleName() + pageNumToShow);
        }
        return pair;
    }*/

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }
}
