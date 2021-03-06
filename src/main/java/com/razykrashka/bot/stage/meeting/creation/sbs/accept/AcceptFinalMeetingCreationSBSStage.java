package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.constants.Telegraph;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.service.config.property.meeting.MeetingProperties;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.FinalMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.loading.LoadingThreadV2;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptFinalMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Value("${razykrashka.group.id}")
    String groupChatId;
    @Autowired
    MeetingProperties meetingProperties;

    @Override
    public void handleRequest() {
        messageManager
                .disableKeyboardLastBotMessage()
                .replyLastMessage("Please, confirm meeting creation \uD83E\uDD28");
        razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).sendSimple();
    }

    @Override
    public void processCallBackQuery() {
        LoadingThreadV2 loadingThread = startLoadingThread(true);

        Meeting meeting = super.getMeetingInCreation();
        CreationState creationState = meeting.getCreationState();
        creationState.setCreationStatus(CreationStatus.DONE);
        creationState.setActiveStage(null);
        creationState.setInCreationProgress(false);
        creationStateRepository.save(creationState);

        meeting.setCreationDateTime(LocalDateTime.now());
        meeting.setCreationState(creationState);
        meeting.getParticipants().add(updateHelper.getUser());
        meetingRepository.save(meeting);

        joinToThread(loadingThread);
        messageManager
                .deleteLastBotMessage()
                .sendRandomSticker("success")
                .sendSimpleTextMessage(getFormatString("success", meeting.getId()));

        if (meetingProperties.getCreation().getNotificationGroup()) {
            String meetingInfo = meetingMessageUtils.createMeetingInfoGroup(meeting);

            InlineKeyboardMarkup keyboard = keyboardBuilder
                    .getKeyboard()
                    .setRow(new InlineKeyboardButton()
                            .setText("What's that?! " + Emoji.FACE_WITH_RAISED_EYEBROW)
                            .setUrl(Telegraph.EN_FAQ))
                    .build();

            messageManager.sendMessage(new SendMessage()
                    .setReplyMarkup(keyboard)
                    .setParseMode(ParseMode.HTML)
                    .setChatId(groupChatId)
                    .setText(meetingInfo)
                    .disableWebPagePreview());
        }
    }

    @Override
    public boolean isStageActive() {
        return (super.isStageActive()
                || updateHelper.isCallBackDataContains())
                && !updateHelper.isCallBackDataContains(EDIT);
    }
}