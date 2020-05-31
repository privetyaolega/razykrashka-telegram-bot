package com.razykrashka.bot.integration.discord;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource(value = "classpath:/props/api.yaml", factory = YamlPropertyLoaderFactory.class)
public class DiscordBot {

    @Value("${discord.bot.token}")
    String token;
    @Value("${discord.guild.id}")
    Long guildId;
    @Value("${discord.guild.category}")
    String meetingInProgressCategoryName;
    @Value("${discord.guild.default-role}")
    String defaultRoleName;
    @Value("${discord.guild.invite-link-template}")
    String inviteLinkTemplate;

    JDA jda;
    Guild guild;
    Role everyoneRole;
    Category meetingInProgressCategory;

    @SneakyThrows
    @PostConstruct
    public void init() {
        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .build()
                .awaitReady();
        guild = jda.getGuildById(guildId);
        everyoneRole = guild.getRolesByName(defaultRoleName, true).iterator().next();
        meetingInProgressCategory = guild.getCategories().stream()
                .filter(c -> c.getName().equalsIgnoreCase(meetingInProgressCategoryName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Not found category " + meetingInProgressCategoryName));
    }

    public String createVoiceMeetingChannel(Meeting m) {
        String channelName = "meeting # " + m.getId() + " - " + m.getMeetingInfo().getTopic();
        VoiceChannel channel = guild.createVoiceChannel(channelName)
                .setParent(meetingInProgressCategory)
                .setUserlimit(m.getMeetingInfo().getParticipantLimit())
                .complete();
        channel.putPermissionOverride(everyoneRole)
                .setDeny(Arrays.asList(
                        Permission.MANAGE_CHANNEL,
                        Permission.MANAGE_PERMISSIONS,
                        Permission.MANAGE_WEBHOOKS,
                        Permission.VOICE_MUTE_OTHERS,
                        Permission.VOICE_MOVE_OTHERS,
                        Permission.VOICE_DEAF_OTHERS))
                .complete();

        String inviteLink = String.format(inviteLinkTemplate, channel.getManager().getChannel()
                .createInvite()
                .setMaxAge(2L, TimeUnit.HOURS)
                .setTemporary(true)
                .complete()
                .getCode());

        log.info("DISCORD: Discord voice channel '{}' has been created. Invite link: {}", channelName, inviteLink);
        return inviteLink;
    }

    public void deleteChannel(int meetingId) {
        guild.getVoiceChannels().stream()
                .filter(c -> c.getName().contains("meeting # " + meetingId + " - "))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(""))
                .delete().complete();
    }
}