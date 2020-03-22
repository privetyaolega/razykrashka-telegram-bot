package com.razykrashka.bot.db.entity.razykrashka.meeting;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "creation_state")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CreationState {
    @Id
    @GeneratedValue
    Integer id;
    @Enumerated(EnumType.STRING)
    CreationStatus creationStatus;
    LocalDateTime startCreationDateTime;
    String activeStage;
    boolean inCreationProgress;
}