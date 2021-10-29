package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Rewind extends Command
{
    private static Rewind INSTANCE;

    private Rewind()
    {
        commandName = "rewind";
        arguments = "(S) / (M:S) / (H:M:S)";
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
        if (event.getMember().getVoiceState().inVoiceChannel())
        {
            String argument = event.getMessage().getContentRaw().split(" ")[1];
            PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.setTrackTime(argument);
            Info.getInstance().execute(event);
        }
    }
}

