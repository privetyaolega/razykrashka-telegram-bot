package com.razykrashka.bot.stage.meeting.creation;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class SelectWayMeetingCreationStage extends MainStage {

    public SelectWayMeetingCreationStage() {
        stageInfo = StageInfo.SELECT_WAY_MEETING_CREATION;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        StageInfo.CREATE_MEETING_BY_TEMPLATE_STAGE.getKeyword(), this.getStageInfo().getStageName(),
                        StageInfo.CREATE_MEETING_ON_STEPS.getKeyword(), this.getStageInfo().getStageName()))
                .build();
    }

    @Override
    public void handleRequest() {
        messageSender.sendSimpleTextMessage(this.getStringMap().get("enMain"), this.getKeyboard());
    }

    @Override
    public boolean processCallBackQuery() {
        messageSender.updateMessage("YOOOHOO, Let's create new meeting together! \uD83D\uDE0D");
        razykrashkaBot.getContext().getBean(CreateMeetingByTemplateStage.class).handleRequest();
        return true;
    }
}