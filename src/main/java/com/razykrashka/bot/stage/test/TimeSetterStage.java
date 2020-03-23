//package com.razykrashka.bot.stage.test;
//
//import com.exigen.istf.timesetter.client.TimeSetterClient;
//import com.razykrashka.bot.stage.MainStage;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
//@Log4j2
//@Component
//public class TimeSetterStage extends MainStage {
//
//    @Override
//    public void handleRequest() {
//        messageManager.sendSimpleTextMessage(new TimeSetterClient().getDateTime().toString());
//
//        new TimeSetterClient().setDateTime(LocalDateTime.now()
//                .plusDays(Long.parseLong(updateHelper.getMessageText().replace("/ts", ""))));
//    }
//
//    @Override
//    public boolean isStageActive() {
//        return updateHelper.isMessageContains("/ts");
//    }
//}