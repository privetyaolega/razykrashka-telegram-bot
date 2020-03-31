package com.razykrashka.bot.db.entity.razykrashka.poll;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "poll")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramPoll {
    @Id
    @GeneratedValue
    Integer id;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "poll")
    Set<TelegramPollOption> telegramPollOptions = new HashSet<>();

    LocalDateTime creationDateTime;
    String question;
    String telegramId;
    boolean isAnonymous;
    boolean isMultipleAnswers;
    Integer totalVoterCount;

    public TelegramPoll(Poll poll) {
        this.telegramId = poll.getId();
        this.question = poll.getQuestion();
        this.isAnonymous = poll.getAnonymous();
        this.isMultipleAnswers = poll.getAllowMultipleAnswers();
        this.totalVoterCount = poll.getTotalVoterCount();
        this.creationDateTime = LocalDateTime.now();
    }
}