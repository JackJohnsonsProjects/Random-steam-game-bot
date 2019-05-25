package com.discord.randsteamgamebot.domain;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;

import static com.discord.randsteamgamebot.utils.BotUtils.STEAM_API_KEY;

public class SteamUser {

    private Logger logger = LoggerFactory.getLogger(SteamUser.class);

    private String displayName;
    private String steam64Id;
    private String profileURL;
    private int totalGames;
    private IUser discordRequester;
    private IChannel userChannel;

    private SteamUser() {

    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSteam64Id() {
        return steam64Id;
    }

    public void setSteam64Id(String steam64Id) {
        this.steam64Id = steam64Id;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public IUser getDiscordRequester() {
        return discordRequester;
    }

    public void setDiscordRequester(IUser discordRequester) {
        this.discordRequester = discordRequester;
    }

    public IChannel getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(IChannel userChannel) {
        this.userChannel = userChannel;
    }

    /**
     * Check if the users profile is private, "timecreated" only returns if it's not private
     * @return Whether or not the profile is private
     */
    private boolean profileIsPrivate() {
        try {
            HttpResponse<JsonNode> response = Unirest.get("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + STEAM_API_KEY +
                    "&steamids=" + steam64Id).asJson();

            JSONObject userInfo = response.getBody().getObject().getJSONObject("response");
            JSONArray jsonResponse = userInfo.getJSONArray("players");

            if (jsonResponse.toString().contains("timecreated")) {
                return false;
            }
            return true;
        } catch (UnirestException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Get the users specific display name from Steam
     * @return The users Steam display name
     */
    /*private static String retrieveUsersDisplayName(String profileURL) {
        try {
            String title = Jsoup.connect(profileURL).get().title();

            if (title.isEmpty()) { return null; }

            String steamName = title.substring(19);
            if (steamName.equalsIgnoreCase("error")) {
                return null;
            }
            return steamName;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }*/

    private String retrieveUsersDisplayName() {
        try {
            HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.get("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + STEAM_API_KEY +
                    "&steamids=" + steam64Id).asJson();

            JSONObject response = jsonNodeHttpResponse.getBody().getObject().getJSONObject("response");
            String steamName = response.getJSONArray("players").getJSONObject(0).get("personaname").toString();
            return steamName;
        } catch (UnirestException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    /**
     * Get the users specific Steam 64 ID, required for the Steam Web API
     * @param profileURL The URL of the users profile
     * @return The users steam 64 ID
     */
    private static String retrieveUsersSteam64ID(String profileURL) {
        try {
            Document doc = Jsoup.connect(profileURL + "/?xml=1").parser(Parser.xmlParser()).get();
            return doc.select("steamID64").text();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Attempt to create a steam user
     * @param profileID The users steam profile ID/name
     */
    public static SteamUser createSteamUser(String profileID) {
        SteamUser steamUser = new SteamUser();
        String steamProfileURL;

        if (profileID.matches("\\d+")) {
            steamProfileURL = "http://steamcommunity.com/profiles/" + profileID;
            steamUser.setSteam64Id(profileID);
        } else {
            steamProfileURL = "http://steamcommunity.com/id/" + profileID;
            steamUser.setSteam64Id(retrieveUsersSteam64ID(steamProfileURL));
        }

        steamUser.setProfileURL(steamProfileURL);
        steamUser.setDisplayName(steamUser.retrieveUsersDisplayName());

        if (steamUser.profileIsPrivate()) {
            return null;
        }

        return steamUser;
    }
}
