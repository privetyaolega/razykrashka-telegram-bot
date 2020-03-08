package com.razykrashka.bot.ui.helpers.sender;

import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter
@Setter
@Log4j2
public abstract class Sender {

    @Autowired
    RazykrashkaBot razykrashkaBot;
    @Autowired
    UpdateHelper updateHelper;

}