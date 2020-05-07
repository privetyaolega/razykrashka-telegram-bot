package com.razykrashka.bot.stage.meeting.edit.delete;

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
    public void processCallBackQuery() {
        Integer id = updateHelper.getIntegerPureCallBackData();
        meeting = meetingRepository.findById(id).get();

        sendDeleteNotification(meeting.getParticipants());
        meetingRepository.delete(meeting);
        messageManager
                .deleteLastBotMessage()
                .sendRandomSticker("sad")
                .sendSimpleTextMessage(getString("main"));
    }

    private void sendDeleteNotification(Set<TelegramUser> participants) {
        String participantsToString = participants.stream()
                .map(this::getUserString)
                .collect(Collectors.joining(","));

        String message = getFormatString("deleteNotification", meeting.getId());
        participants.stream()
                .filter(p -> !p.getId().equals(meeting.getTelegramUser().getId()))
                .forEach(p -> {
                    String id = String.valueOf(p.getId());
                    messageManager
                            .disableKeyboardLastBotMessage(id)
                            .sendMessage(new SendMessage()
                                    .setChatId(id)
                                    .setText(message)
                                    .enableMarkdown(true));
                });

        log.info("Meeting # {} has been deleted. Owner: {}, participants: [{}]",
                meeting.getId(), meeting.getTelegramUser().getId(), participantsToString);
    }

    private String getUserString(TelegramUser u) {
        return u.getId() + " " + (Optional.ofNullable(u.getUserName()).isPresent() ? u.getUserName() : "");
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}