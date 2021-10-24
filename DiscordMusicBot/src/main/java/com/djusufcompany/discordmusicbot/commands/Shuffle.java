package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Shuffle extends Command
{
    private static Shuffle INSTANCE;

    private Shuffle()
    {
        commandName = "shuffle";
        description = "Перемешать очередь";
    }

    public static Shuffle getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Shuffle();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.shuffle();
        Queue.getInstance().execute(event);
    }
}

