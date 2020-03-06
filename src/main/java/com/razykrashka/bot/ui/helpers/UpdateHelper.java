package com.razykrashka.bot.ui.helpers;

import com.razykrashka.bot.service.RazykrashkaBot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class UpdateHelper {

	@Autowired
	protected RazykrashkaBot razykrashkaBot;

	public boolean isCallBackDataContains(String string) {
		if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
			return razykrashkaBot.getRealUpdate()
					.getCallbackQuery().getData()
					.contains(string);
		}
		return false;
	}

	public boolean isCallBackDataEquals(String string) {
		if (razykrashkaBot.getRealUpdate().hasCallbackQuery()) {
			return razykrashkaBot.getRealUpdate()
					.getCallbackQuery().getData()
					.equals(string);
		}
		return false;
	}

	public boolean isMessageContains(String string) {
		if (razykrashkaBot.getRealUpdate().hasMessage()) {
			return razykrashkaBot.getRealUpdate()
					.getMessage().getText()
					.contains(string);
		}
		return false;
	}

	public boolean isMessageTextEquals(String string) {
		if (razykrashkaBot.getRealUpdate().hasMessage()) {
			return razykrashkaBot.getRealUpdate()
					.getMessage().getText()
					.equals(string);
		}
		return false;
	}

	public Long getChatId() {
		Update update = razykrashkaBot.getRealUpdate();
		if (update.hasMessage()) {
			return update.getMessage().getChat().getId();
		} else {
			return update.getCallbackQuery().getMessage().getChat().getId();
		}
	}
}