package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Remove extends Command
{
    private static Remove INSTANCE;

    private Remove()
    {
        commandName = "remove";
        arguments = "(ID) / (ID-ID)";
        description = "Удаление трека из очереди по номеру";
    }

    public static Remove getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Remove();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        if (event.getMember().getVoiceState().inVoiceChannel())
        {
            String message = event.getMessage().getContentRaw().substring(2 + commandName.length());
            String indexes[] = message.split("-");
            Integer start = Math.min(Integer.valueOf(indexes[0]), Integer.valueOf(indexes[indexes.length - 1]));
            Integer end = Math.max(Integer.valueOf(indexes[0]), Integer.valueOf(indexes[indexes.length - 1]));
            for (int id = end; id >= start; id -= 1)
            {
                PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.remove(id);
            }
            Queue.getInstance().execute(event);
        }
    }
}

