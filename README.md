# Velocity Telegram Bridge

## Features

- Forwards chat messages from Telegram to Minecraft and vice versa.
- Configurable message formats for different events such as join, leave, and server change.

## Commands and Permissions

The plugin provides the following commands:

- `/vtb reload`: Reads and reloads the plugin configuration from the `config.toml` file. Requires `vtb.reload` permission.

## Installation

1. Download the latest version of the plugin from the [releases page](https://github.com/feeeek/VelocityTelegramBridge/releases).
2. Place the downloaded jar file into your Velocity `plugins` folder.
3. Restart your Velocity proxy.
4. The plugin will generate a default `config.toml` file inside `velocity-telegram-bridge` folder. Modify this file with your Telegram API credentials and desired settings.
5. Restart your Velocity proxy or reload the plugin with `/vtb reload` command.

> [!NOTE]
> If you want to handle death and advancement messages, you need to install [YepLib](https://github.com/unilock/YepLib) plugin on velocity and [YepTwo](https://github.com/unilock/YepTwo) mod for backend servers

## Configuration

The `config.toml` file contains the following sections:

- `Telegram`: Contains the token and chat_id for your Telegram bot.
- `Events`: Contains the message formats for different events. You can enable or disable each event and customize the message format.

This is the default configuration:

```toml
#Telegram bot api configuration
[Telegram]
    #Telegram bot api token
    token = "0:ABCDEFG"
    #Telegram chat id to send messages to
    chat_id = 0
#Events configuration
[Events]
    #Should messages from chat be sent to the chat?
    message_enabled = true
    #Format of the message
    #Available placeholders: {player}, {message}
    message_format = "<b>{player}</b>: {message}"
    #Should messages from telegram be sent to the server?
    message_from_telegram_enabled = true
    #Format of the message
    #Available placeholders: {author}, {message}
    message_from_telegram_format = "{author}: {message}"
    #Should join messages be sent to the chat?
    join_enabled = true
    #Format of the message
    #Available placeholders: {player}
    join_format = "<b>{player} joined the server</b>"
    #Should leave messages be sent to the chat?
    leave_enabled = true
    #Format of the message
    #Available placeholders: {player}
    leave_format = "<b>{player} left the server</b>"
    #Should server change messages be sent to the chat?
    server_change_enabled = true
    #Format of the message
    #Available placeholders: {server}, {new_server}, {player}
    server_change_format = "<b>{player} moved from {server} to {new_server}</b>"
    #Should status messages be sent to the chat?
    status_enabled = true
    #Format of the message
    #Available placeholders: {player_count}, {players}
    status_format = "There are <b>{player_count}</b> players online:\n{players}"
    #Should death messages be sent to the chat?
    #Note: This requires YepLib installed on velocity and YepTwo on backend servers
    death_enabled = true
    #Format of the message
    #Available placeholders: {death_message}
    death_format = "<b>{death_message}</b>"
    #Should advancement messages be sent to the chat?
    advancement_enabled = true
    #Format of the message
    #Available placeholders: {player}, {advancement_title}, and {advancement_description}
    advancement_format = "<b>{player}</b> has made the advancement {advancement_title}\n<i>{advancement_description}</i>"
```
