package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptLevelMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
public class LevelMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow("Elementary", "Elementary")
                .setRow("Pre-Intermediate", "Pre_Intermediate")
                .setRow("Intermediate", "Intermediate")
                .setRow("Upper-Intermediate", "Upper_Intermediate")
                .setRow("Advanced", "Advanced")
                .setRow("BACK TO LOCATION EDIT", LocationMeetingCreationSBSStage.class.getSimpleName())
                .build();
        messageSender.updateMessage(getMeetingPrettyString() + "\n\nPlease, input speaking level.", keyboardMarkup);
        setActiveNextStage(AcceptLevelMeetingCreationSBSStage.class);
    }
}