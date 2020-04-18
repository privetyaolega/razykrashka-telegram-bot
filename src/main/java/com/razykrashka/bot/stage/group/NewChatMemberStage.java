package com.razykrashka.bot.stage.group;


import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
public class NewChatMemberStage extends MainStage {

    @Override
    public void handleRequest() {
        List<User> newChatMembers = updateHelper.getUpdate()
                .getMessage()
                .getNewChatMembers();
        logNewUser(newChatMembers);
        newChatMembers.forEach(m -> messageManager.sendMessage(new SendMessage()
                .setChatId(String.valueOf(m.getId()))
                .setText(getString("welcome"))));
    }

    private String getNewMembersString(List<User> newChatMembers) {
        return newChatMembers.stream()
                .map(this::getUserString)
                .collect(Collectors.joining(",", "[", "]"));
    }

    private String getUserString(User u) {
        String userName = Optional.ofNullable(u.getUserName()).isPresent() ? " " + u.getUserName() : "";
        return u.getId() + userName;
    }

    private boolean isSelfVisited() {
        Update update = updateHelper.getUpdate();
        Integer idFrom = update.getMessage().getFrom().getId();
        return update.getMessage().getNewChatMembers().get(0).getId().equals(idFrom);
    }

    private void logNewUser(List<User> newChatMembers) {
        String mainUser = getUserString(updateHelper.getUpdate().getMessage().getFrom());
        if (isSelfVisited()) {
            log.info("NEW MEMBER: New chat member: {}", mainUser);
        } else {
            String usersString = getNewMembersString(newChatMembers);
            log.info("NEW MEMBERS: Main user: {}, invited members: {}",
                    mainUser, usersString);
        }
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isNewChatMember();
    }
}