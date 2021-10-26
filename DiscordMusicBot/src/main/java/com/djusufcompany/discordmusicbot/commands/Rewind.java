package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;



public class Rewind extends Command
{
    private static Rewind INSTANCE;

    private Rewind()
    {
        commandName = "rewind";
        arguments = "(s) / (m.s) / (h.m.s)";
        description = "Перемотать текущий трек в указанную позицию";
    }

    public static Rewind getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Rewind();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        String argument = event.getMessage().getContentRaw().split(" ")[1];
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.setTrackTime(argument);
        Info.getInstance().execute(event);
    }
}
