package com.razykrashka.bot.stage.meeting.edit.delete;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteSingleMeetingStage extends MainStage {

    Meeting meeting;

    @Override
    public boolean processCallBackQuery() {
        Integer id = updateHelper.getIntegerPureCallBackData();
        meeting = meetingRepository.findById(id).get();

        sendDeleteNotification(meeting.getParticipants());
        meetingRepository.delete(meeting);
        messageManager
                .disableKeyboardLastBotMessage()
                .updateMessage("Meeting has been deleted " + Emoji.PENSIVE)
                .sendRandomSticker("sad");
        return true;
    }

    private void sendDeleteNotification(Set<TelegramUser> participants) {
        String participantsToString = participants.stream()
                .map(this::getUserString)
                .collect(Collectors.joining(","));

        String message = getFormatString("deleteNotification", meeting.getId());
        participants.stream()
                .filter(p -> !p.getTelegramId().equals(meeting.getTelegramUser().getTelegramId()))
                .forEach(p -> messageManager
                        .disableKeyboardLastBotMessage()
                        .sendMessage(new SendMessage()
                                .setParseMode(ParseMode.HTML)
                                .setChatId(String.valueOf(p.getTelegramId()))
                                .setText(message)));

        log.info("Meeting # {} has been deleted. Owner: {}, participants: [{}]",
                meeting.getId(), meeting.getTelegramUser().getTelegramId(), participantsToString);
    }

    private String getUserString(TelegramUser u) {
        return u.getTelegramId() + " " + (Optional.ofNullable(u.getUserName()).isPresent() ? u.getUserName() : "");
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}