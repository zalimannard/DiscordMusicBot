package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
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
        if (event.getMember().getVoiceState().inVoiceChannel())
        {
            TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;
            if (scheduler.getCurrentTrackNumber() != scheduler.getQueueSize())
            {
                scheduler.jumpTo(scheduler.getCurrentTrackNumber() + 1);
            }
            else if (scheduler.isQueueLooped())
            {
                scheduler.jumpTo(1);
            }
            else
            {
                scheduler.endTrack();
            }
        }
    }
}

