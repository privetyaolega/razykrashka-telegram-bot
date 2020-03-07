package com.razykrashka.bot.ui.helpers.keyboard;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@Setter
@Builder
public class PaginationKeyboardHelper {

    List<Pair<String, String>> list;
    Class classCaller;
    int pageNumToShow;
    int totalPagesSize;

    protected List<Pair<String, String>> getPaginationKeyboard() {
        list = new ArrayList<>();
        if (pageNumToShow <= 3) {
            getLowLimitKeyboard();
        } else if (pageNumToShow + 3 > totalPagesSize) {
            getHighLimitKeyboard();
        } else {
            list.add(Pair.of("« 1", getCallBackQuery(1)));
            list.add(Pair.of("‹ " + (pageNumToShow - 1), getCallBackQuery(pageNumToShow - 1)));
            list.add(getCallBackPair(pageNumToShow));
            list.add(Pair.of((pageNumToShow + 1) + " ›", getCallBackQuery(pageNumToShow + 1)));
            list.add(Pair.of(totalPagesSize + " »", getCallBackQuery(totalPagesSize)));
        }
        highlightPageNumToShow();
        return list;
    }

    private void getLowLimitKeyboard() {
        if (totalPagesSize <= 5) {
            IntStream.rangeClosed(1, totalPagesSize).forEach(i -> list.add(getCallBackPair(i)));
        } else {
            IntStream.rangeClosed(1, 4).forEach(i -> list.add(getCallBackPair(i)));
            list.add(Pair.of(totalPagesSize + " »", getCallBackQuery(totalPagesSize)));
        }
    }

    private void getHighLimitKeyboard() {
        list.add(Pair.of("« 1", getCallBackQuery(1)));
        list.add(getCallBackPair(totalPagesSize - 3));
        list.add(getCallBackPair(totalPagesSize - 2));
        list.add(getCallBackPair(totalPagesSize - 1));
        list.add(getCallBackPair(totalPagesSize));
    }

    private void highlightPageNumToShow() {
        IntStream.range(0, list.size()).forEach(index -> {
            if (list.get(index).getFirst().equals(String.valueOf(pageNumToShow))) {
                Pair<String, String> pair = list.get(index);
                list.set(index, Pair.of(String.format("⋅%s⋅", pair.getFirst()), pair.getSecond()));
            }
        });
    }

    private String getCallBackQuery(int pageNum) {
        return classCaller.getSimpleName() + pageNum;
    }

    private Pair<String, String> getCallBackPair(int pageNum) {
        String pageNumStr = String.valueOf(pageNum);
        return Pair.of(pageNumStr, classCaller.getSimpleName() + pageNumStr);
    }
}