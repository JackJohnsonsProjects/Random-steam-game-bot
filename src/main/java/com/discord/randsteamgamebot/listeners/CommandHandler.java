package com.discord.randsteamgamebot.listeners;

import com.discord.randsteamgamebot.command.*;
import com.discord.randsteamgamebot.command.botinfo.ServerCountCommand;
import com.discord.randsteamgamebot.command.game.LeastPlayedCommand;
import com.discord.randsteamgamebot.command.game.MostPlayedCommand;
import com.discord.randsteamgamebot.domain.BotCommandArgs;
import com.discord.randsteamgamebot.domain.BotUser;
import com.discord.randsteamgamebot.randomizer.GameRandomizer;
import com.discord.randsteamgamebot.utils.BotUtils;
import com.discord.randsteamgamebot.utils.ErrorMessages;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler {

    private List<BotCommand> commands = new ArrayList<>();
    private GameRandomizer gameRandomizer = new GameRandomizer();

    public CommandHandler() {
        commands.add(new MostPlayedCommand());
        commands.add(new LeastPlayedCommand());
        commands.add(new ServerCountCommand());
        /*commands.add(new RandGameCommand());
        commands.add(new RandGameFilterCommand());
        commands.add(new RandGameTagCommand());
        commands.add(new RandGameGenreCommand());
        commands.add(new SbHelpCommand());*/
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] argArray = event.getMessage().getContent().split(" ");
        if (event.getAuthor().isBot()) {
            return ;
        }

        if (argArray.length == 0) {
            return ;
        }

        if (!argArray[0].startsWith(BotUtils.BOT_PREFIX)) {
            return ;
        }

        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.set(0, argsList.get(0).substring(1));

        BotUser botUser = BotUser.createSteamUser(argsList.get(1));

        if (botUser == null) {
            event.getChannel().sendMessage(ErrorMessages.PRIVATE_PROFILE);
            return ;
        }

        botUser.setDiscordRequester(event.getAuthor());
        botUser.setUserChannel(event.getChannel());

        BotCommandArgs botCommandArgs = new BotCommandArgs();
        botCommandArgs.setCommandNames(argsList);
        botCommandArgs.setEvent(event);
        botCommandArgs.setGameRandomizer(gameRandomizer);
        botCommandArgs.setBotUser(botUser);

        for (BotCommand command : commands) {
            if (command.matches(argsList)) {
                command.execute(botCommandArgs);
            }
        }
    }

}
