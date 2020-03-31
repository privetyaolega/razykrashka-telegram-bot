package com.razykrashka.bot.db.entity.razykrashka.poll;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "poll_option")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramPollOption{
    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    TelegramPoll poll;
    String textOption;
    Integer count;
}