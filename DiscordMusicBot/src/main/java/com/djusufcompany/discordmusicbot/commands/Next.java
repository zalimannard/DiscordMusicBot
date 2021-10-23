package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public abstract class Next
{
    public static void execute(MessageReceivedEvent event)
    {
        PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.skip();
    }
}

