package com.razykrashka.bot.db.entity.razykrashka.meeting;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meeting")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDateTime creationDateTime;
    LocalDateTime meetingDateTime;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    TelegramUser telegramUser;

    @OneToOne()
    @JoinColumn(name = "meeting_info_id")
    MeetingInfo meetingInfo;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "location_id")
    Location location;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "creation_state_id")
    CreationState creationState;

    @Enumerated(EnumType.STRING)
    MeetingFormatEnum format;

    @Column
    @ManyToMany
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<TelegramUser> participants = new HashSet<>();
}