package com.razykrashka.bot.db.entity.razykrashka.meeting;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
    @GeneratedValue
    Integer id;

    LocalDateTime startCreationDateTime;
    LocalDateTime creationDateTime;
    LocalDateTime meetingDateTime;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    TelegramUser telegramUser;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "meeting_info_id")
    MeetingInfo meetingInfo;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "location_id")
    Location location;

    @Enumerated(EnumType.STRING)
    CreationStatus creationStatus;

    @Column
    @ManyToMany
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<TelegramUser> participants = new HashSet<>();
}