package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Skip extends Command
{
    private static Skip INSTANCE;

    private Skip()
    {
        commandName = "skip";
        description = "Переход к следующему треку в очереди";
    }

    public static Skip getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Skip();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.skip();
    }
}

