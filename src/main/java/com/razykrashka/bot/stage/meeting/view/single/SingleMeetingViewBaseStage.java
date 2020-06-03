package com.razykrashka.bot.stage.meeting.view.single;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.service.MeetingService;
import com.razykrashka.bot.service.config.property.meeting.MeetingProperties;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.edit.delete.DeleteConfirmationSingleMeetingStage;
import com.razykrashka.bot.stage.meeting.view.all.ActiveMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.ArchivedMeetingsViewStage;
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
import java.util.stream.IntStream;

@Log4j2
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class SingleMeetingViewBaseStage extends MainStage {

    Meeting meeting;
    @Autowired
    MeetingService meetingService;
    @Autowired
    MeetingProperties meetingProperties;
    @Autowired
    MeetingMessageUtils meetingMessageUtils;
    final String joinStage = SingleMeetingViewJoinStage.class.getSimpleName();
    final String leaveStage = SingleMeetingViewLeaveStage.class.getSimpleName();
    final String contactStage = SingleMeetingViewContactStage.class.getSimpleName();
    final String deleteStage = DeleteConfirmationSingleMeetingStage.class.getSimpleName();
    final String topicStage = SingleMeetingTopicInfoStage.class.getSimpleName();
    final String mapStage = SingleMeetingViewMapStage.class.getSimpleName();
    final String mainStage = SingleMeetingViewMainStage.class.getSimpleName();
    public static final String KEYWORD = "/meeting";

    protected Integer getMeetingId() {
        if (updateHelper.hasMessage()) {
            return updateHelper.getIntDataFromMessage();
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

    protected boolean isMeetingStarted() {
        LocalDateTime meetingDateTime = meeting.getMeetingDateTime();
        return LocalDateTime.now().isAfter(meetingDateTime.minusMinutes(15));
    }

    protected boolean hasFreePlaces() {
        int participants = meeting.getParticipants().size();
        Integer participantLimit = meeting.getMeetingInfo().getParticipantLimit();
        return participants < participantLimit;
    }

    protected Pair<String, String> getActionButton() {
        Pair<String, String> button = null;
        if (!isMeetingStarted()) {
            if (isUserParticipant()) {
                button = Pair.of("Leave " + Emoji.DISAPPOINTED_RELIEVED, leaveStage + meeting.getId());
            } else if (hasFreePlaces()) {
                button = Pair.of("Join " + Emoji.ROCK_HAND, joinStage + meeting.getId());
            }
        }
        return button;
    }

    protected Pair<String, String> getDeleteButton() {
        Pair<String, String> button = null;
        if (!isMeetingStarted()) {
            if (isUserOwner()) {
                button = Pair.of("Delete " + Emoji.RED_CROSS, deleteStage + meeting.getId());
            }
        }
        return button;
    }

    protected abstract List<Pair<String, String>> getMeetingInfoButtons();

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder
                .getKeyboard()
                .setRow(getMeetingInfoButtons())
                .setRow(getActionButton())
                .setRow(getDeleteButton())
                .build();
    }

    protected Pair<String, String> getNavigationBackButton() {
        //TODO: update to work with meeting.getCreationState().getCreationStatus()
        Pair<String, String> pair;
        List<Meeting> meetings;
        if (meeting.getMeetingDateTime().isAfter(LocalDateTime.now())) {
            meetings = meetingService.getAllCreationStatusDone();
        } else {
            meetings = meetingService.getAllArchivedMeetings();
        }
        int indexOfMeeting = IntStream.range(0, meetings.size())
                .filter(i -> meetings.get(i).getId().equals(meeting.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Meeting is absent in appropriate list. ID:" + meeting.getId()));

        if (meeting.getMeetingDateTime().isAfter(LocalDateTime.now())) {
            int pageNumToShow = (int) Math.ceil(indexOfMeeting / new Double(meetingProperties.getViewPerPage()));
            pair = Pair.of(Emoji.LEFT_FINGER + " Active meetings", ActiveMeetingsViewStage.class.getSimpleName() + pageNumToShow);
        } else {
            int pageNumToShow = (int) Math.ceil(indexOfMeeting / 4.0);
            pair = Pair.of(Emoji.LEFT_FINGER + " Archived meetings", ArchivedMeetingsViewStage.class.getSimpleName() + pageNumToShow);
        }
        return pair;
    }

    /*private Pair<String, String> getNavigationBackButton() {
        @Value("${razykrashka.bot.meeting.view-per-page}")
        Integer meetingsPerPage;
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
    public void processCallBackQuery() {
        handleRequest();
    }
}
