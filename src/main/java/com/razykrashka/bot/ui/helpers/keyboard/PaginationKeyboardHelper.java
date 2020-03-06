package com.razykrashka.bot.ui.helpers.keyboard;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
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
		if (pageNumToShow < 5) {
			getLowLimitKeyboard();
		} else if (pageNumToShow + 3 > totalPagesSize) {
			getHighLimitKeyboard();
		} else {
			list.add(Pair.of("« 1", getCallBackQuery(1)));
			list.add(Pair.of("‹ " + (pageNumToShow - 1), getCallBackQuery(pageNumToShow - 1)));
			list.add(Pair.of(String.format("⋅%s⋅", pageNumToShow), getCallBackQuery(pageNumToShow)));
			list.add(Pair.of((pageNumToShow + 1) + " ›", getCallBackQuery(pageNumToShow + 1)));
			list.add(Pair.of(totalPagesSize + " »", getCallBackQuery(totalPagesSize)));
			System.out.println(list);
		}
		return list;
	}

	private void getLowLimitKeyboard() {
		IntStream.range(1, totalPagesSize).forEach(i -> {
			if (i == pageNumToShow) {
				list.add(Pair.of(String.format("⋅%s⋅", i), getCallBackQuery(i)));
				return;
			}
			list.add(Pair.of(String.valueOf(i), getCallBackQuery(i)));
		});
		list.add(Pair.of(String.valueOf(totalPagesSize), getCallBackQuery(totalPagesSize)));
	}

	private void getHighLimitKeyboard() {
		list.add(Pair.of("« 1", getCallBackQuery(1)));
		list.add(Pair.of(String.valueOf(totalPagesSize - 3), getCallBackQuery(totalPagesSize - 3)));
		list.add(Pair.of(String.valueOf(totalPagesSize - 2), getCallBackQuery(totalPagesSize - 2)));
		list.add(Pair.of(String.valueOf(totalPagesSize - 1), getCallBackQuery(totalPagesSize - 1)));
		list.add(Pair.of(String.valueOf(totalPagesSize), getCallBackQuery(totalPagesSize)));
		list = list.stream()
				.map(el -> el.getFirst().equals(pageNumToShow) ? Pair.of(el.getFirst(), el.getSecond()) :
						Pair.of(String.format("⋅%s⋅", el.getFirst()), el.getSecond()))
				.collect(Collectors.toList());
	}

	private String getCallBackQuery(int pageNum) {
		return classCaller.getSimpleName() + pageNum;
	}
}
