package com.discord.randsteamgamebot.domain;
/**
 * Represents a steam game from the users library
 */

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.discord.randsteamgamebot.utils.BotUtils.STEAM_API_KEY;

/**
 *
 * @author Jack
 */
public class Game {

    private static Logger logger = LoggerFactory.getLogger(Game.class);
 
    private String gameName;
    private boolean playedOrNot;
    private String gameID;
    private Integer minutesPlayed;
    private String gamePlayedTime;
    private String installLink;
    private String storePage;
    private String embedLink;
    
    public Game(String gameID, String playedGame, Integer minutesPlayed) {
        this.gameID = gameID;
        this.gameName = playedGame;
        this.minutesPlayed = minutesPlayed;
        this.installLink = "steam://run/" + gameID;
        this.storePage = "http://store.steampowered.com/app/" + gameID;
        this.embedLink = "[" + gameName + "]" + "(" + storePage + ")";
        playTimeToHoursAndMinutes(minutesPlayed);
        this.playedOrNot = true;
    }
    
    public Game(String gameID, String game) {
        this.gameID = gameID;
        this.gameName = game;
        this.minutesPlayed = 0;
        this.installLink = "steam://run/" + gameID;
        this.storePage = "http://store.steampowered.com/app/" + gameID;
        this.embedLink = "[" + gameName + "]" + "(" + storePage + ")";
        playTimeToHoursAndMinutes(minutesPlayed);
        this.playedOrNot = false;
    }
    
    public String getGameName() {
        return gameName;
    }
    
    public boolean getPlayStatus() {
        return playedOrNot;
    }
    
    public Integer getMinutesPlayed() {
        return minutesPlayed;
    }

    public String getGameID() { return gameID; }

    public String getGamePlayedTime() {
        return gamePlayedTime;
    }

    public String getInstallLink() {
        return installLink;
    }

    public String getEmbedLink() {
        return embedLink;
    }

    public String getStorePage() {
        return storePage;
    }

    private void playTimeToHoursAndMinutes(Integer minutes_Played) {
        int hoursPlayed = minutes_Played / 60;
        int minutesPlayed = minutes_Played % 60;
        if (hoursPlayed == 0) {
            this.gamePlayedTime = minutesPlayed + " minute(s)";
            return ;
        }
        this.gamePlayedTime = hoursPlayed + " hour(s) and " + minutesPlayed + " minutes";
    }

    /**
     * Helpful method for choosing a random game
     * @param games The games in which to choose a random game from
     * @return A random game
     */
    public static Game chooseRandGame(List<Game> games) {
        Random r = new Random();
        int rand = r.nextInt(games.size());
        return games.get(rand);
    }

    /**
     * Filter games by whether they've been played or not
     * @param games The games to filter
     * @param played Whether or not the game has been played
     * @return The played or unplayed games for this user
     */
    public static ArrayList<Game> filterGames(List<Game> games, boolean played) {
        ArrayList<Game> temp = new ArrayList<>();
        for (Game game : games) {
            if (game.getPlayStatus() == played) {
                temp.add(game);
            }
        }
        return temp;
    }

    /**
     * Check if the user actually owns any games
     * @param games The games to pass in
     * @return Whether or not at least one game exists
     */
    public static boolean noGamesOwned(List<Game> games) {
        return games == null || games.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (playedOrNot != game.playedOrNot) return false;
        if (gameName != null ? !gameName.equals(game.gameName) : game.gameName != null) return false;
        if (gameID != null ? !gameID.equals(game.gameID) : game.gameID != null) return false;
        if (minutesPlayed != null ? !minutesPlayed.equals(game.minutesPlayed) : game.minutesPlayed != null)
            return false;
        if (gamePlayedTime != null ? !gamePlayedTime.equals(game.gamePlayedTime) : game.gamePlayedTime != null)
            return false;
        if (installLink != null ? !installLink.equals(game.installLink) : game.installLink != null) return false;
        if (storePage != null ? !storePage.equals(game.storePage) : game.storePage != null) return false;
        return embedLink != null ? embedLink.equals(game.embedLink) : game.embedLink == null;
    }

    @Override
    public int hashCode() {
        int result = gameName != null ? gameName.hashCode() : 0;
        result = 31 * result + (playedOrNot ? 1 : 0);
        result = 31 * result + (gameID != null ? gameID.hashCode() : 0);
        result = 31 * result + (minutesPlayed != null ? minutesPlayed.hashCode() : 0);
        result = 31 * result + (gamePlayedTime != null ? gamePlayedTime.hashCode() : 0);
        result = 31 * result + (installLink != null ? installLink.hashCode() : 0);
        result = 31 * result + (storePage != null ? storePage.hashCode() : 0);
        result = 31 * result + (embedLink != null ? embedLink.hashCode() : 0);
        return result;
    }
}
