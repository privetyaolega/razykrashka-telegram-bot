package com.razykrashka.bot.stage.information;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.meeting.view.all.ActiveMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.ArchivedMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.OfflineMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.OnlineMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class HelpStage extends MainStage {

    public final static String KEYWORD = "/help";

    @Override
    public void handleRequest() {
        StringBuilder sb = new StringBuilder()
                .append("Hey! There are some command by which you can control me and get some information about meetings and not only")
                .append(Emoji.WINK).append("\n\n")
                .append(TextFormatter.getBoldString("Meetings\n"))
                .append(ActiveMeetingsViewStage.KEYWORD)
                .append(" - show all active meetings\n")
                .append(OnlineMeetingsViewStage.KEYWORD)
                .append(" - show all active online meetings\n")
                .append(OfflineMeetingsViewStage.KEYWORD)
                .append(" - show all active offline meetings\n")
                .append(ArchivedMeetingsViewStage.KEYWORD)
                .append(" - show all expired (archived) meetings\n")
                .append("/my")
                .append(" - show my planned meetings\n");

        messageManager
                .disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(sb.toString());
    }

    @Override
    public void processCallBackQuery() {
        handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD);
    }
}