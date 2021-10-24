package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Remove extends Command
{
    private static Remove INSTANCE;

    private Remove()
    {
        commandName = "remove";
        arguments = "(id) / (id-id)";
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
        String message = event.getMessage().getContentRaw().substring(2 + commandName.length());
        String indexes[] = message.split("-");
        Integer start = Integer.valueOf(indexes[0]) - 1;
        Integer end = Integer.valueOf(indexes[indexes.length - 1]) - 1;
        for (int id = start; id <= end; id += 1)
        {
            PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.remove(start);
        }
        Queue.getInstance().execute(event);
    }
}

