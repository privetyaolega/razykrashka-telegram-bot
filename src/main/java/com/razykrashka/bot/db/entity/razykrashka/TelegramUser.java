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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "telegramUser")
    Set<Meeting> createdMeetings = new HashSet<>();

    @Column
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "participants")
    Set<Meeting> toGoMeetings = new HashSet<>();

    public void addMeetingTotoGoMeetings(Meeting meeting) {
        toGoMeetings.add(meeting);
    }

    public void removeFromToGoMeetings(Meeting meeting) {
        toGoMeetings.remove(meeting);
    }
}