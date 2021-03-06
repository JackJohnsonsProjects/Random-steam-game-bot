package com.discord.randsteamgamebot.listeners;

import com.discord.randsteamgamebot.utils.BotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.AllUsersReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.GuildUnavailableEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.PermissionUtils;
import sx.blah.discord.util.RequestBuffer;

import java.util.EnumSet;
import java.util.stream.Collectors;

import static com.discord.randsteamgamebot.utils.BotUtils.DELETE_EMOJI;
import static com.discord.randsteamgamebot.utils.BotUtils.deleteMessage;

public class GuildListener {

    private final Logger logger = LoggerFactory.getLogger(GuildListener.class);

    @EventSubscriber
    public void onEmojiReact(ReactionEvent event) {
        if (BotUtils.botLoggedInAndReady(event.getClient())) {
            try {
                IDiscordClient client = event.getClient();
                IUser self = client.getOurUser();
                IUser reactor = event.getUser();
                IMessage message = event.getMessage();
                boolean reactionIsDelete = event.getReaction().getEmoji().equals(DELETE_EMOJI);

                if (event.getUser().equals(self) || !message.getAuthor().equals(self)
                    || !reactionIsDelete || !event.getReaction().getUsers().contains(self)
                ) {
                    return ;
                }

                if (message.getContent().equals("") || reactor.equals(message.getMentions().get(0))) {
                    deleteMessage(message);
                    return ;
                }

                EnumSet<Permissions> permissions = EnumSet.of(Permissions.ADMINISTRATOR, Permissions.MANAGE_MESSAGES, Permissions.MANAGE_CHANNEL, Permissions.MANAGE_SERVER);
                EnumSet<Permissions> permissionsForGuild = reactor.getPermissionsForGuild(event.getGuild());

                if (permissionsForGuild != null) {
                    permissionsForGuild.forEach(permission -> {
                        if (permissions.contains(permission)) {
                            deleteMessage(message);
                        }
                    });
                }
            } catch (Exception ex) {
                logger.info("Failed on a user deleting a message.");
                throw new IllegalStateException(ex);
            }
        }
    }

    @EventSubscriber
    public void onGuildJoined(GuildCreateEvent event) {
        logger.info("I connected to a guild. (ID: {} | Users: {} | Total Guilds: {})",
                event.getGuild().getLongID(), event.getGuild().getUsers().size(), event.getClient().getGuilds().size());
    }

    @EventSubscriber
    public void onGuildLeft(GuildLeaveEvent event) {
        logger.info("I disconnected from a guild. (ID: {} | Users: {} | Total Guilds: {})",
                event.getGuild().getLongID(), event.getGuild().getUsers().size(), event.getClient().getGuilds().size());
    }
}
