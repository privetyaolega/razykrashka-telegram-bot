package com.razykrashka.bot.db.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "meeting")
@Getter
@Setter
@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue
    Long id;

    LocalDateTime creationDateTime;
    LocalDateTime meetingDateTime;

    @Embedded
    MeetingInfoEmbeddable meetingInfo;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    TelegramUser owner;

    @Column
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<TelegramUser> participants;
}