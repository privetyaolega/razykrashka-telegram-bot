package com.razykrashka.bot.stage.meeting.creation.sbs;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Log4j2
@Getter
@Setter
public abstract class BaseMeetingCreationSBSStage extends MainStage {

    private Meeting meeting;

    @Override
    public boolean isStageActive() {
        return super.getStageActivity();
    }

    public String getMeetingPrettyString() {
        StringBuilder sb = new StringBuilder();
        meeting = getMeetingInCreation();

        if (meeting.getMeetingDateTime() != null) {
            sb.append("DATE: ").append(this.meeting.getMeetingDateTime()
                    .format(DateTimeFormatter.ofPattern("dd MMMM (EEEE)", Locale.ENGLISH)));
        }

        if (meeting.getMeetingDateTime() != null && meeting.getMeetingDateTime().getHour() != 0) {
            sb.append("\n\nTIME: ").append(this.meeting.getMeetingDateTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        if (meeting.getLocation() != null) {
            sb.append("\n\nADDRESS: " + meeting.getLocation().getLocationLink());
        }

        if (meeting.getMeetingInfo() != null) {
            sb.append("\n\nLEVEL: " + meeting.getMeetingInfo().getSpeakingLevel());
        }

        if (meeting.getMeetingInfo() != null && meeting.getMeetingInfo().getParticipantLimit() != null) {
            sb.append("\n\nPARTICIPANT LIMIT: " + meeting.getMeetingInfo().getParticipantLimit());
        }

        if (meeting.getMeetingInfo() != null && meeting.getMeetingInfo().getTopic() != null) {
            sb.append("\n\nTOPIC: " + meeting.getMeetingInfo().getTopic());
        }

        if (meeting.getMeetingInfo() != null && meeting.getMeetingInfo().getQuestions() != null) {
            sb.append("\n\nQUESTION: \n" + meeting.getMeetingInfo().getQuestions());
        }

        sb.append("\n\n\n\n");

        return sb.toString();
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    protected void setActiveNextStage(Class clazz) {
        razykrashkaBot.getStages().forEach(stage -> stage.setActive(false));
        Stage stage = ((Stage) razykrashkaBot.getContext().getBean(clazz));
        stage.setActive(true);
    }

    protected Meeting getMeetingInCreation() {
        List<Meeting> meetingsInCreation = meetingRepository.findAllByCreationStatusEqualsAndTelegramUser(
                CreationStatus.IN_PROGRESS, razykrashkaBot.getUser());
        if (meetingsInCreation.size() == 0) {
            meeting = new Meeting();
            meeting.setCreationStatus(CreationStatus.IN_PROGRESS);
            meeting.setTelegramUser(razykrashkaBot.getUser());
            return meeting;
        }
        return meetingsInCreation.get(0);
    }
}