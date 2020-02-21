package com.razykrashka.bot.db.entity;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
class MeetingInfoEmbeddable {
    @Enumerated(EnumType.STRING)
    SpeakingLevel speakingLevel;
    String topic;
    String questions;
}