package com.razykrashka.bot.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Topic {
    String id;
    String topic;
    String level;
    String questions;
}