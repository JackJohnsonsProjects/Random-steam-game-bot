package com.discord.randsteamgamebot.randomizer;

/*
    This class is used to crawl the Steam page of a user
    to retrieve their games and Steam display name, provides
    methods to return a random game
*/

import com.discord.randsteamgamebot.domain.Game;
import com.discord.randsteamgamebot.domain.BotUser;
import com.discord.randsteamgamebot.utils.BotUtils;
import com.discord.randsteamgamebot.utils.ErrorMessages;
import com.discord.randsteamgamebot.utils.GameGenres;
import com.discord.service.GameService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.discord.randsteamgamebot.utils.BotUtils.editMessage;

/**
 *
 * @author Jack
 */
public class GameRandomizer {

    private Logger logger = LoggerFactory.getLogger(GameRandomizer.class);

    private GameService gameService = GameService.getInstance();

    public GameRandomizer() {

    }

    /**
     * Choose a random game for the user to play.
     */
    public void randGame(BotUser botUser) {
        try {
            IMessage message = BotUtils.sendInitialMessage(botUser.getUserChannel(), botUser.getDiscordRequester()).get();
            ArrayList<Game> allGames = gameService.getAllUsersGames(botUser.getSteam64Id());

            if (Game.noGamesOwned(allGames)) {
                BotUtils.editMessage(message, botUser, ErrorMessages.NO_GAMES_ERR);
                return ;
            }

            botUser.setTotalGames(allGames.size());
            Game randGame = Game.chooseRandGame(allGames);
            String storePage = "http://store.steampowered.com/app/" + randGame.getGameID();

            BotUtils.editMessage(message, botUser.getDisplayName() + " owns " + allGames.size() + " games.\n"
                    + "I'd recommend " + botUser.getDisplayName() + " plays **" + randGame.getGameName() + "**.\n" +
                    "Install or play the game: " + randGame.getInstallLink() + " or go to the store page: " + storePage);

            logger.info("Successfully returned played game " + randGame.getGameName() + " for profile: " + botUser.getSteam64Id()
                    + ", user owns " + botUser.getTotalGames() + " games.");
        } catch (Exception ex) {
            logger.info("Failed to retrieve a random game for the user");
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Choose and return a random game for the user that they have already played before,
     * will also display the amount of games that they have played already along with
     * the percentage of games they have played
     */
    public void randPlayedGame(BotUser botUser) {
        try {
            IMessage message = BotUtils.sendInitialMessage(botUser.getUserChannel(), botUser.getDiscordRequester()).get();
            ArrayList<Game> allGames = gameService.getAllUsersGames(botUser.getSteam64Id());

            if (Game.noGamesOwned(allGames)) {
                BotUtils.editMessage(message, botUser, ErrorMessages.NO_GAMES_ERR);
                return ;
            }

            botUser.setTotalGames(allGames.size());
            ArrayList<Game> playedGames = Game.filterGames(allGames, true);

            if (Game.noGamesOwned(playedGames)) {
                BotUtils.editMessage(message, botUser, "This user hasn't played any games yet or their privacy setting is hiding the game play time.");
                return ;
            }

            int playedGameVal = playedGames.size();
            float gamePlayedPercent = (playedGameVal * 100.0f) / botUser.getTotalGames();

            Game randPlayedGame = Game.chooseRandGame(playedGames);
            String storePage = "http://store.steampowered.com/app/" + randPlayedGame.getGameID();

            BotUtils.editMessage(message, botUser, botUser.getDisplayName() + " has played " + playedGameVal + " of their games out of "
                    + botUser.getTotalGames() + " (" + gamePlayedPercent + "%)" + ".\n"
                    + "I recommend that " + botUser.getDisplayName() + " plays **" + randPlayedGame.getGameName()
                    + "**.\nThere is currently " + randPlayedGame.getGamePlayedTime() + " played on this game.\n"
                    + "Install or play the game: " + randPlayedGame.getInstallLink() + " or go to the store page: " + storePage);

            logger.info("Successfully returned played game " + randPlayedGame.getGameName() + " for profile: " + botUser.getSteam64Id()
                    + ", user owns " + botUser.getTotalGames() + " games.");
        } catch (Exception ex) {
            logger.info("Failed to retrieve a random played game for the user.");
            throw new IllegalStateException(ex);
        }


    }

    /**
     * Choose and return a random game for the user that they have not yet played,
     * will also display the amount of games that they haven't played along with
     * the percentage of games they have not yet played
     */
    public void randUnplayedGame(BotUser botUser) {
        try {
            IMessage message = BotUtils.sendInitialMessage(botUser.getUserChannel(), botUser.getDiscordRequester()).get();
            ArrayList<Game> allGames = gameService.getAllUsersGames(botUser.getSteam64Id());

            if (Game.noGamesOwned(allGames)) {
                BotUtils.editMessage(message, botUser, ErrorMessages.NO_GAMES_ERR);
                return ;
            }

            botUser.setTotalGames(allGames.size());
            ArrayList<Game> unplayedGames = Game.filterGames(allGames, false);

            if (Game.noGamesOwned(unplayedGames)) {
                BotUtils.editMessage(message, botUser, "All of the games on this account have already been played.");
                return ;
            }

            int unplayedGameVal = unplayedGames.size();
            float gamePlayedPercent = (unplayedGameVal * 100.0f) / botUser.getTotalGames();

            Game randUnplayedGame = Game.chooseRandGame(unplayedGames);

            BotUtils.editMessage(message, botUser, botUser.getDisplayName() + " hasn't played " + unplayedGameVal + " of their games out of "
                    + botUser.getTotalGames() + " (" + gamePlayedPercent + "%)" + ".\n"
                    + "I recommend that " + botUser.getDisplayName() + " plays **" + randUnplayedGame.getGameName() + "**.\n"
                    + "Install or play the game: " + randUnplayedGame.getInstallLink() + " or go to the store page: " + randUnplayedGame.getStorePage());

            logger.info("Successfully returned played game " + randUnplayedGame.getGameName() + " for profile: " + botUser.getSteam64Id()
                    + ", user owns " + botUser.getTotalGames() + " games.");
        } catch (Exception ex) {
            logger.info("Failed to retrieve a random unplayed game for the user.");
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Return the users most played games on Steam,
     * nicely formatted and then output to them
     */
    public void mostPlayedGames(BotUser botUser) {
        try {
            IMessage message = BotUtils.sendInitialMessage(botUser.getUserChannel(), botUser.getDiscordRequester()).get();

            ArrayList<Game> allGames = gameService.getAllUsersGames(botUser.getSteam64Id());

            if (Game.noGamesOwned(allGames)) {
                BotUtils.editMessage(message, botUser, ErrorMessages.NO_GAMES_ERR);
                return ;
            }

            botUser.setTotalGames(allGames.size());
            ArrayList<Game> playedGames = Game.filterGames(allGames, true);

            if (playedGames.size() < 5) {
                BotUtils.editMessage(message, botUser, "This user needs to have played at least five games to use this command.");
                return ;
            }

            EmbedObject embedObject = BotUtils.createEmbedBuilder(playedGames, "Most played games for " + botUser.getDisplayName() + "\n",
                    "The top five most played games on Steam for this user.", true);

            BotUtils.editMessage(message, botUser, embedObject);

            logger.info("Successfully returned most played games for profile: " + botUser.getSteam64Id()
                    + ", user owns " + botUser.getTotalGames() + " games.");
        } catch (Exception ex) {
            logger.info("Failed to retrieve most played games for the user.");
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Return the users 5 least played games on Steam which at least have
     * some time logged against them
     */
    public void leastPlayedGames(BotUser botUser) {
        try {
            IMessage message = BotUtils.sendInitialMessage(botUser.getUserChannel(), botUser.getDiscordRequester()).get();

            ArrayList<Game> allGames = gameService.getAllUsersGames(botUser.getSteam64Id());

            if (Game.noGamesOwned(allGames)) {
                BotUtils.editMessage(message, botUser, ErrorMessages.NO_GAMES_ERR);
                return ;
            }

            botUser.setTotalGames(allGames.size());
            ArrayList<Game> playedGames = Game.filterGames(allGames, true);

            if (playedGames.size() < 5) {
                BotUtils.editMessage(message, botUser, "This user needs to have played at least five games to use this command.");
                return ;
            }

            EmbedObject embedObject = BotUtils.createEmbedBuilder(playedGames, "Least played games for " + botUser.getDisplayName() + "\n",
                    "The top five least played games on Steam for this user. (With playtime)", false);

            BotUtils.editMessage(message, botUser, embedObject);

            logger.info("Successfully returned least played games for profile: " + botUser.getSteam64Id()
                    + ", user owns " + botUser.getTotalGames() + " games.");
        } catch (Exception ex) {
            logger.info("Failed to retrieve the least played games for the user.");
        }
    }

    /**
     * Return a random game that the user owns based on genre
     * @param genre The genre to search
     */
    public void randGameByGenre(BotUser botUser, String genre) {
        try {
            IMessage message = BotUtils.sendInitialMessage(botUser.getUserChannel(), botUser.getDiscordRequester()).get();

            String genreVal = GameGenres.gameGenreMap.get(genre);
            if (genreVal == null) {
                BotUtils.editMessage(message, botUser, BotUtils.embedBuilderForGenre("!rgame <steamname> <genre>"));
                return ;
            }

            ArrayList<Game> allGames = gameService.getAllUsersGames(botUser.getSteam64Id());

            if (Game.noGamesOwned(allGames)) {
                BotUtils.editMessage(message, botUser, ErrorMessages.NO_GAMES_ERR);
                return ;
            }

            botUser.setTotalGames(allGames.size());

            HttpResponse<JsonNode> response = Unirest.get("https://steamspy.com/api.php?request=genre&genre=" + URLEncoder.encode(genreVal, "UTF-8")).asJson();
            JSONObject object = response.getBody().getObject();

            if (object.length() == 0) {
                BotUtils.editMessage(message, botUser, ErrorMessages.STEAM_SPY_DOWN);
                return ;
            }

            List<Game> gamesByGenre = allGames.stream()
                    .filter(game -> object.has(game.getGameID()))
                    .collect(Collectors.toList());

            if (Game.noGamesOwned(gamesByGenre)) {
                BotUtils.editMessage(message, botUser, "You don't own any games from the " + genre + " genre.");
                return ;
            }

            Game randomGameFromGenre = Game.chooseRandGame(gamesByGenre);

            String storePage = "http://store.steampowered.com/app/" + randomGameFromGenre.getGameID();

            BotUtils.editMessage(message, botUser, botUser.getDisplayName() + " owns " + gamesByGenre.size() + " game(s) in the **" + genreVal + "** genre.\n"
                    + "I'd recommend " + botUser.getDisplayName() + " plays **" + randomGameFromGenre.getGameName() + "**.\n" +
                    "Install or play the game: " + randomGameFromGenre.getInstallLink() + " or go to the store page: " + storePage);

            logger.info("Successfully returned game from genre " + genre + " " + randomGameFromGenre.getGameName() + " for profile: " + botUser.getSteam64Id()
                    + ", user owns " + botUser.getTotalGames() + " games.");
        } catch (Exception ex) {
            logger.error("Exception thrown in getting a random game by genre.");
            throw new IllegalStateException(ex);
        }
    }

    public void randGameByTag(BotUser botUser, String tag) {
        try {
            IMessage message = BotUtils.sendInitialMessage(botUser.getUserChannel(), botUser.getDiscordRequester()).get();

            if (tag == null || tag.equals("")) {
                BotUtils.editMessage(message, botUser, BotUtils.embedBuilderForTags("!rgame <steamname> tag <tagname>"));
                return ;
            }

            ArrayList<Game> allGames = gameService.getAllUsersGames(botUser.getSteam64Id());

            if (Game.noGamesOwned(allGames)) {
                BotUtils.editMessage(message, botUser, ErrorMessages.NO_GAMES_ERR);
                return ;
            }

            botUser.setTotalGames(allGames.size());

            HttpResponse<JsonNode> response = Unirest.get("https://steamspy.com/api.php?request=tag&tag=" + URLEncoder.encode(tag, "UTF-8")).asJson();
            JSONObject object = response.getBody().getObject();

            if (object.length() == 0) {
                BotUtils.editMessage(message, botUser, ErrorMessages.STEAM_SPY_DOWN);
                return ;
            }

            List<Game> gamesByTag = allGames.stream()
                    .filter(game -> object.has(game.getGameID()))
                    .collect(Collectors.toList());

            if (Game.noGamesOwned(gamesByTag)) {
                BotUtils.editMessage(message, botUser, "You don't own any games from the " + tag + " tag.");
                return ;
            }

            Game randGameFromTag = Game.chooseRandGame(gamesByTag);

            String storePage = "http://store.steampowered.com/app/" + randGameFromTag.getGameID();

            BotUtils.editMessage(message, botUser,
                    "" + botUser.getDisplayName() + " owns " + gamesByTag.size() + " game(s) in the **" + tag + "** tag. Remember that tags" +
                            " are applied to games by steam users, so there will sometimes be discrepancies.\n"
                    + "I'd recommend " + botUser.getDisplayName() + " plays **" + randGameFromTag.getGameName() + "**.\n" +
                    "Install or play the game: " + randGameFromTag.getInstallLink() + " or go to the store page: " + storePage);

            logger.info("Successfully returned game from tag " + tag + " " + randGameFromTag.getGameName() + " for profile: " + botUser.getSteam64Id()
                    + ", user owns " + botUser.getTotalGames() + " games.");
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

    }
}
