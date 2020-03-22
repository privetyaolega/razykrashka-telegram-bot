package com.razykrashka.bot.stage.meeting.view.single;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptLocationMeetingCreationStepByStep;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptTimeMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.SingleMeetingViewStage;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
public class SingleMeetingParticipantsListStage extends MainStage {

    private Meeting meeting;

    @Override
    public boolean processCallBackQuery() {
/*
        Integer meetingId = updateHelper.getIntegerPureCallBackData();
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Can not find meeting with id: " + meetingId));
        Optional<TelegramUser> user = Optional.ofNullable(meeting.getTelegramUser());
        user.ifPresent(x -> messageManager.sendContact(x));
*/

        String newMessage = Arrays.stream(messageManager.getSendMessage().getText().split("\n")).limit(5).collect(Collectors.joining("\n"));

        String message = messageManager.getSendMessage().getText();
        int meetingId = Integer.parseInt(message.substring(message.indexOf("#") + 1, message.indexOf("</code>")).trim());

        meeting = meetingRepository.findMeetingById(meetingId);

        String participants = meeting.getParticipants().stream()
                .map(this::getSingleStringForParticipantsList)
                .collect(Collectors.joining("\n"));

        newMessage = newMessage.concat("\n" + participants);

        messageManager.updateMessage(newMessage, this.getKeyboard());

        return true;
    }

    private String getSingleStringForParticipantsList(TelegramUser telegramUser) {
        String participantName = telegramUser.getFirstName() + telegramUser.getLastName();
        String participantUsername = "";
        if (!telegramUser.getUserName().equals("")) {
            participantUsername = "(" + "@" + telegramUser.getUserName() + ")";
        }

        return participantName + participantUsername;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        KeyboardBuilder builder = keyboardBuilder.getKeyboard();
        if (razykrashkaBot.getUser().getToGoMeetings().stream().anyMatch(m -> m.getId().equals(meeting.getId()))) {
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