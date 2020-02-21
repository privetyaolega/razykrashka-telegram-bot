package com.razykrashka.bot.db.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "meeting_info")
@Getter
@Setter
@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingInfo {
    @Id
    @GeneratedValue
    Integer id;

    @Enumerated(EnumType.STRING)
    SpeakingLevel speakingLevel;
    String topic;
    @Lob
    String questions;
}