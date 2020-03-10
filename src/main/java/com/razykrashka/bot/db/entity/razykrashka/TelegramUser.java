package com.razykrashka.bot.db.entity.razykrashka;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    Integer telegramId;

    @Column
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "telegramUser") // cascade = {CascadeType.PERSIST, CascadeType.REFRESH}
    Set<Meeting> createdMeetings = new HashSet<>();

    @Column
    @ManyToMany(fetch = FetchType.LAZY) //  cascade = {CascadeType.PERSIST, CascadeType.REFRESH}
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