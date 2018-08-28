package com.discord.randsteamgamebot.utils;

import com.discord.randsteamgamebot.domain.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.Comparator;
import java.util.List;

public class BotUtils {

    private static Logger logger = LoggerFactory.getLogger(BotUtils.class);

    public final static String BOT_PREFIX = "!";
    public static final ReactionEmoji DELETE_EMOJI = ReactionEmoji.of("❌");

    public static IDiscordClient createClient(String token) {
        return new ClientBuilder()
                .withToken(token)
                .withRecommendedShardCount()
                .setMaxReconnectAttempts(100000)
                .build();

    }

    public static EmbedObject embedBuilderForGenre(String title, String desc, String errorMessage) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withColor(41, 128, 185);
        embedBuilder.withTitle(title);

        embedBuilder.appendField("Possible genres: \n",
                "1.     " + GameGenres.gameGenreMap.get("early access") + "     " + "\n" +
                        "2.    " + GameGenres.gameGenreMap.get("action") + "     " + "\n" +
                        "3.    " + GameGenres.gameGenreMap.get("strategy") + "     " + "\n" +
                        "4.    " + GameGenres.gameGenreMap.get("indie") + "     " + "\n" +
                        "5.    " + GameGenres.gameGenreMap.get("rpg") + "     " + "\n" +
                        "6.    " + GameGenres.gameGenreMap.get("racing") + "     " + "\n" +
                        "7.    " + GameGenres.gameGenreMap.get("sports") + "     " + "\n" +
                        "8.    " + GameGenres.gameGenreMap.get("free") + "     " + "\n" +
                        "9.  " + GameGenres.gameGenreMap.get("adventure") + "     " + "\n" +
                        "10.   " + GameGenres.gameGenreMap.get("simulation") + "     " + "\n" +
                        "11.  " + GameGenres.gameGenreMap.get("casual") + "     " + "\n" +
                        "12.  " + GameGenres.gameGenreMap.get("multiplayer") + "     ", true);

        return embedBuilder.build();
    }

    public static EmbedBuilder createEmbedBuilder(List<Game> games, String title, String desc, boolean mostPlayed) {
        if (mostPlayed) {
            games.sort(Comparator.comparingInt(Game::getMinutesPlayed).reversed());
        } else {
            games.sort(Comparator.comparingInt(Game::getMinutesPlayed));
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withColor(41, 128, 185);
        embedBuilder.withTitle(title);
        embedBuilder.withDesc(desc);

        embedBuilder.appendField("Game Name",
                games.get(0).getEmbedLink() + "\n" +
                        games.get(1).getEmbedLink() + "   "   + "\n" +
                        games.get(2).getEmbedLink() + "   "   + "\n" +
                        games.get(3).getEmbedLink() + "   "   + "\n" +
                        games.get(4).getEmbedLink(), true);

        embedBuilder.appendField( "Time Played",
                games.get(0).getGamePlayedTime() + "\n" +
                        games.get(1).getGamePlayedTime() + "\n" +
                        games.get(2).getGamePlayedTime() + "\n" +
                        games.get(3).getGamePlayedTime() + "\n" +
                        games.get(4).getGamePlayedTime(), true);

        return embedBuilder;
    }

    // Build and display the list of commands to the user
    public static void commandList(IChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(41, 128, 185);
        builder.appendDescription("**Important: Your Steam Privacy settings will affect the outcome, so try to make everything public.**\n\n"
                                    + "Grabs all of a users games on Steam and selects a random game."
                                    + " User can filter whether or not they want a random game they haven't played before. Can also see top played and least played games. **New feature to filter by genre.**");
        builder.appendField("Commands   ", "!rgame <name/17 digit ID>"
                                    + "           " + "\n!rgame <name/17 digit ID> <played/unplayed>"
                                    + "           " + "\n!rgame <name/17 digit ID> <genre>"
                                    + "           " + "\n!mostplayed <name/17 digit ID>"
                                    + "           " + "\n!leastplayed <name/17 digit ID>"
                                    + "           " + "\n\nGithub link: https://git.io/vxnPL", true);
        builder.appendField("Example", "!rgame Xufoo\n!rgame 76561198054740594 played\n!rgame Xufoo action\n!mostplayed Xufoo\n!leastplayed Xufoo", true);


        RequestBuffer.request(() -> channel.sendMessage(builder.build()).addReaction(DELETE_EMOJI));
    }
}
