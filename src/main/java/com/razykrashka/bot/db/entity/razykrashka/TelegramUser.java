package com.razykrashka.bot.db.entity.razykrashka;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramUser {

    @Id
    @GeneratedValue
    Integer id;
    String userName;
    String lastName;
    String firstName;
    String phoneNumber;
    String skypeContact;
    Integer telegramId;

    @Column
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "telegramUser")
    Set<Meeting> createdMeetings = new HashSet<>();

    @Column
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meeting_id")
    )
    Set<Meeting> toGoMeetings = new HashSet<>();

    public void addMeetingTotoGoMeetings(Meeting meeting) {
        toGoMeetings.add(meeting);
    }

    public void removeFromToGoMeetings(Meeting meeting) {
        toGoMeetings.remove(meeting);
    }
}