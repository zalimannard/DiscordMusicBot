package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Prev extends Command
{
    private static Prev INSTANCE;

    private Prev()
    {
        commandName = "prev";
        description = "Воспроизвести предыдущий трек";
    }

    public static Prev getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Prev();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler;
        scheduler.jumpTo(scheduler.getCurrentTrackNumber() - 1);
    }
}

