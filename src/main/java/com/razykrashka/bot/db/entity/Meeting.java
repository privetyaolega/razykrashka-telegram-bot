package com.razykrashka.bot.db.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Component
public class Meeting {

    @Id
    @GeneratedValue
    Integer id;

    LocalDateTime creationDateTime;
    LocalDateTime meetingDateTime;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    TelegramUser telegramUser;

    @OneToOne
    @JoinColumn(name = "meeting_info_id")
    MeetingInfo meetingInfo;

    @OneToOne
    @JoinColumn(name = "location_id")
    Location location;

    @Column
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<TelegramUser> participants = new ArrayList<>();
}