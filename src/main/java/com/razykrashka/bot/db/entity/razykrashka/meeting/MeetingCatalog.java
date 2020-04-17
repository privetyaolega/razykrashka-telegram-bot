package com.razykrashka.bot.db.entity.razykrashka.meeting;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "meeting_catalog")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingCatalog {
    @Id
    Integer id;
    @Enumerated(EnumType.STRING)
    SpeakingLevel speakingLevel;
    String topic;
    @Lob
    String questions;
}