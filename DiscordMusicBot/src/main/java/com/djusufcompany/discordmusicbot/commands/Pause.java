package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Pause extends Command
{
    private static Pause INSTANCE;

    private Pause()
    {
        commandName = "pause";
        description = "Остановить трек";
    }

    public static Pause getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Pause();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.pause();
    }
}

