package com.razykrashka.bot.stage.meeting.view;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class AllMeetingViewStage extends MainStage {

    List<Meeting> modelList = new ArrayList<>();

    public AllMeetingViewStage() {
        stageInfo = StageInfo.ALL_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        modelList = StreamSupport.stream(meetingRepository.findAll().spliterator(), false)
                .filter(meeting -> meeting.getCreationStatus().equals(CreationStatus.DONE))
                .collect(Collectors.toList());
        if (modelList.size() == 0) {
            messageManager.sendSimpleTextMessage("NO MEETINGS :(");
        } else {
            String messageText = modelList.stream().skip(0).limit(20)
                    .map(model -> model.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm",
                            Locale.ENGLISH)) + "\n"
                            + "\uD83D\uDCCD" + model.getLocation().getLocationLink().toString() + "\n"
                            + model.getMeetingInfo().getSpeakingLevel().toString() + "\n"
                            + model.getMeetingInfo().getTopic() + "\n"
                            + "INFORMATION: /meeting" + model.getId())
                    .collect(Collectors.joining(getStringMap().get("delimiterLine"),
                            "\uD83D\uDCAB Найдено " + modelList.size() + " встреч(и)\n\n", ""));
            if (modelList.size() > 5) {
                //TODO: PAGINATION INLINE KEYBOARD
            }
            messageManager.sendSimpleTextMessage(messageText);
        }
    }

    @Override
    public boolean isStageActive() {
        Message message = razykrashkaBot.getRealUpdate().getMessage();
        if (message == null) {
            return false;
        } else {
            boolean res = message.getText().equals("View Meetings");
            return res;
        }
    }

    public void getPaginationKeyboard(int currentPageNum, int totalPagesSize) {
        List<Pair<String, String>> list = new ArrayList<>();
        if (currentPageNum < 5) {
            printKeyBoardLow(currentPageNum, totalPagesSize);
        } else if (currentPageNum + 3 > totalPagesSize) {
            printKeyBoardHigh(currentPageNum, totalPagesSize);
        } else {
            list.add(Pair.of("« 1", getCallBackQuery(1)));
            list.add(Pair.of("‹ " + (currentPageNum - 1), getCallBackQuery(currentPageNum - 1)));
            list.add(Pair.of(String.format("⋅%s⋅", currentPageNum), getCallBackQuery(currentPageNum)));
            list.add(Pair.of((currentPageNum + 1) + " ›", getCallBackQuery(currentPageNum + 1)));
            list.add(Pair.of(totalPagesSize + " »", getCallBackQuery(totalPagesSize)));
            System.out.println(list);
        }
    }

    private void printKeyBoardLow(int currentPageNum, int totalPagesSize) {
        List<Pair<String, String>> list = new ArrayList<>();
        IntStream.range(1, 5).forEach(i -> {
            if (i == currentPageNum) {
                list.add(Pair.of(String.format("⋅%s⋅", i), getCallBackQuery(i)));
                return;
            }
            list.add(Pair.of(String.valueOf(i), getCallBackQuery(i)));
        });
        list.add(Pair.of(String.valueOf(totalPagesSize), getCallBackQuery(totalPagesSize)));
    }

    public void printKeyBoardHigh(int currentPageNum, int totalPagesSize) {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(Pair.of("« 1", getCallBackQuery(1)));
        list.add(Pair.of(String.valueOf(totalPagesSize - 3), getCallBackQuery(totalPagesSize - 3)));
        list.add(Pair.of(String.valueOf(totalPagesSize - 2), getCallBackQuery(totalPagesSize - 2)));
        list.add(Pair.of(String.valueOf(totalPagesSize - 1), getCallBackQuery(totalPagesSize - 1)));
        list.add(Pair.of(String.valueOf(totalPagesSize), getCallBackQuery(totalPagesSize)));
    }

    private String getCallBackQuery(int pageNum) {
        return this.getClass().getSimpleName() + pageNum;
    }
}
