package com.razykrashka.bot.stage.meeting.view.single;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Arrays;
import java.util.stream.Collectors;

@Log4j2
@Component
public class SingleMeetingParticipantsListStage extends MainStage {

    public static final String NO_PARTICIPANTS = "Nobody Participate in this meeting";

    private Meeting meeting;

    @Override
    public boolean processCallBackQuery() {
        String oldMessageWithQuestionList = getPreviousMessageText();
        String newMessageWithoutQuestionList = Arrays.stream(oldMessageWithQuestionList.split("\n"))
                .limit(5)
                .collect(Collectors.joining("\n"));

        Integer meetingId = updateHelper.getIntegerPureCallBackData();
        meeting = meetingRepository.findMeetingById(meetingId);

        String participants = meeting.getParticipants().stream()
                .map(this::getSingleStringForParticipantsList)
                .collect(Collectors.joining("\n"));

        String message = participants.isEmpty() ? NO_PARTICIPANTS : participants;
        newMessageWithoutQuestionList = newMessageWithoutQuestionList.concat("\n").concat(message);
        messageManager.updateMessage(newMessageWithoutQuestionList, this.getKeyboard());
        return true;
    }

    private String getPreviousMessageText() {
        Long chatId = messageManager.getUpdateHelper().getChatId();
        return telegramMessageRepository.findTop1ByChatIdOrderByIdDesc(chatId).getText();
    }

    private String getSingleStringForParticipantsList(TelegramUser telegramUser) {
        String participantName = telegramUser.getFirstName() + " " + telegramUser.getLastName();
        String participantUsername = "";
        if (!telegramUser.getUserName().isEmpty()) {
            participantUsername = " (" + "@" + telegramUser.getUserName() + ")\n";
        }
        return participantName + participantUsername;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        KeyboardBuilder builder = keyboardBuilder.getKeyboard();
        if (updateHelper.getUser().getToGoMeetings().stream().anyMatch(m -> m.getId().equals(meeting.getId()))) {
            builder.setRow("Unsubscribe", SingleMeetingViewUnsubscribeStage.class.getSimpleName() + meeting.getId());
        } else {
            Integer participants = meeting.getParticipants().size();
            Integer participantLimit = meeting.getMeetingInfo().getParticipantLimit();

            //TODO remove first statement and add participants limit to all meetings
            if ((participants == null || participants == 0)
                    || (participantLimit != null
                    && participants != null
                    && participants < participantLimit)) {
                builder.setRow("Join", SingleMeetingViewJoinStage.class.getSimpleName() + meeting.getId());
            }
        }
        return builder
                .setRow(ImmutableMap.of(
                        "Contact", SingleMeetingViewContactStage.class.getSimpleName() + meeting.getId(),
                        "Main Info", SingleMeetingViewStage.class.getSimpleName() + meeting.getId(),
                        "Map", SingleMeetingViewMapStage.class.getSimpleName() + meeting.getId()))
                .build();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}