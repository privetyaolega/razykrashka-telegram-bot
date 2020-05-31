package com.razykrashka.bot.ui.helpers.sender;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantsMessageManager extends MessageManager {

    public ParticipantsMessageManager sendMessageToAllParticipants(Meeting meeting, String message) {
        return sendMessageToAllParticipants(meeting, message, null);
    }

    public ParticipantsMessageManager sendMessageToAllParticipants(Meeting meeting, String message, InlineKeyboardMarkup keyboard) {
        meeting.getParticipants().forEach(p -> {
            String id = String.valueOf(p.getId());
            disableKeyboardLastBotMessage(id)
                    .sendRandomSticker("greeting", p.getId())
                    .sendMessage(new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(id)
                            .setText(message)
                            .setReplyMarkup(keyboard));
        });
        return this;
    }

    public ParticipantsMessageManager sendMessageToAllParticipants(Meeting meeting, SendMessage sendMessage) {
        meeting.getParticipants().forEach(p -> {
            String id = String.valueOf(p.getId());
            disableKeyboardLastBotMessage(id)
                    .sendMessage(sendMessage.setChatId(id));
        });
        return this;
    }
}