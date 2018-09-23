[![Discord Bots](https://discordbots.org/api/widget/status/348109452043485185.svg)](https://discordbots.org/bot/348109452043485185)

# Random Steam Game Bot #
A simple bot for discord to give a user a random game to play from their Steam profile. A user can also filter by games they've played or games they haven't yet played. Another feature is the capability of checking your most played or least played games.

See [Commands](#commands) for usages.

## Important ##
Since Steam updated what users can decide to show on their profile, I have no possible way to prevent every edge case in the bot. An ideal scenario would be a level of privacy indicated in the Steam API response, but this is currently not provided.

If you want to avoid any issues just ensure you have your privacy settings set to "Public".

## Current version ## 
Few additions I would like to make in due time -

- Refactor the way commands are currently handled (maybe)

## Hosting ##

The bot is now being hosted and can be invited to discord servers.

[Invite the bot directly](https://discordapp.com/oauth2/authorize?client_id=348109452043485185&permissions=67324928&scope=bot
)

[Find it on bots.discord.pw](https://bots.discord.pw/bots/348109452043485185)

[Find it on discordbots](https://discordbots.org/bot/348109452043485185)

## Commands ##

```
!sbhelp

!rgame <Custom URL/17 digit ID>

!rgame <Custom URL/17 digit ID> <played/unplayed>

!rgame <Custom URL/17 digit ID> <genre>

!rgame <Custom URL/17 digit ID> tag <tagName>

!mostplayed <Custom URL/17 digit ID>

!leastplayed <Custom URL/17 digit ID>
```