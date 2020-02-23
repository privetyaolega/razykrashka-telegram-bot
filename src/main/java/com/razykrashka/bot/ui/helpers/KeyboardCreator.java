package com.razykrashka.bot.ui.helpers;

import com.razykrashka.bot.service.RazykrashkaBot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Log4j2
public class KeyboardCreator {

    RazykrashkaBot razykrashkaBot;
    ReplyKeyboard keyboard;

    public KeyboardCreator(RazykrashkaBot razykrashkaBot) {
        this.razykrashkaBot = razykrashkaBot;
    }

    public KeyboardCreator createKeyboard() {
        return this;
    }
}