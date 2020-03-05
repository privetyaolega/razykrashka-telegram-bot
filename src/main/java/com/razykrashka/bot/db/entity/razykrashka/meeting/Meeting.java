package com.razykrashka.bot.db.entity.razykrashka.meeting;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
@Component
public class Meeting {
    @Id
    @GeneratedValue
    Integer id;

    LocalDateTime creationDateTime;
    LocalDateTime meetingDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    TelegramUser telegramUser;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meeting_info_id")
    MeetingInfo meetingInfo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    Location location;

    @Enumerated(EnumType.STRING)
    CreationStatus creationStatus;

    @Column
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<TelegramUser> participants;

    public void addParticipant(TelegramUser user) {
        participants.add(user);
    }
    public void removeParticipant(TelegramUser user) {
        participants.remove(user);
    }
}