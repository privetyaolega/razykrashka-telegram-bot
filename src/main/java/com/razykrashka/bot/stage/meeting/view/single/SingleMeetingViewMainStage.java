package com.razykrashka.bot.stage.meeting.view.single;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SingleMeetingViewMainStage extends SingleMeetingViewBaseStage {

    @Override
    public void handleRequest() {
        Integer id = getMeetingId();
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (!optionalMeeting.isPresent()) {
            String message = super.getFormatString("meetingNotFound", id);
            messageManager
//                    .sendRandomSticker("error")
                    .replyLastMessage(message);
            return;
        }
        meeting = optionalMeeting.get();
        String messageText = meetingMessageUtils.createSingleMeetingFullInfo(meeting);
        messageManager.updateOrSendDependsOnLastMessageOwner(messageText, this.getKeyboard());

/*        if (updateHelper.isCallBackQueryFromGroup()) {
            String userChatId = String.valueOf(razykrashkaBot.getRealUpdate().getCallbackQuery().getFrom().getId());
            messageManager
                    .disableKeyboardLastBotMessage(userChatId)
                    .sendMessage(new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(userChatId)
                            .setText(messageText)
                            .setReplyMarkup(this.getKeyboard())
                            .disableWebPagePreview());
        } else {
        }*/
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder
                .getKeyboard()
                .setRow(getMeetingInfoButtons())
                .setRow(getActionButton())
                .setRow(getDeleteButton())
                .build();
    }

    protected List<Pair<String, String>> getMeetingInfoButtons() {
        List<Pair<String, String>> buttonList = new ArrayList<>();
        buttonList.add(Pair.of(Emoji.ONE_PERSON_SILHOUETTE, contactStage + meeting.getId()));
        buttonList.add(Pair.of(Emoji.SPEECH_CLOUD, topicStage + meeting.getId()));

        if (meeting.getFormat().equals(MeetingFormatEnum.OFFLINE)) {
            buttonList.add(Pair.of(Emoji.LOCATION, mapStage + meeting.getId()));
        }
        return buttonList;
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageContains(KEYWORD)
                && !updateHelper.isMessageFromGroup());
    }
}
