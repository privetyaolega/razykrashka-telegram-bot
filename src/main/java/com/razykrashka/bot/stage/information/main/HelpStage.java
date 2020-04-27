package com.razykrashka.bot.stage.information.main;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.meeting.view.all.ActiveMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.ArchivedMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.OfflineMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.OnlineMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class HelpStage extends InformationMainStage {

    public final static String KEYWORD = "/help";

    public HelpStage() {
        buttonLabel = "Help";
    }

    @Override
    public void handleRequest() {
        messageManager
                .disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(getMessage());
    }

    private String getMessage() {
        return new StringBuilder()
                .append("Hey! Here are some useful commands that might help you find your perfect meeting:")
                .append("\n\n")
                .append(TextFormatter.getBoldString("Meetings\n"))
                .append(ActiveMeetingsViewStage.KEYWORD)
                .append(" - show all available meetings\n")
                .append(OnlineMeetingsViewStage.KEYWORD)
                .append(" - show all available online meetings\n")
                .append(OfflineMeetingsViewStage.KEYWORD)
                .append(" - show all available offline meetings\n")
                .append(ArchivedMeetingsViewStage.KEYWORD)
                .append(" - show all expired (archived) meetings\n")
                .append("/my")
                .append(" - show my schedule meetings")
                .append("\n\n")
                .append(TextFormatter.getBoldString("Actions\n"))
                .append("/create")
                .append(" - create new meeting")
                .append("\n\nGood luck! ").append(Emoji.WINK).toString();
    }

    @Override
    public void processCallBackQuery() {
        messageManager.updateMessage(getMessage(), getKeyboardWithHighlightedButton());
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}