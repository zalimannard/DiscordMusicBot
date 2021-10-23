package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Remove extends CommandData implements Command
{
    private static Remove INSTANCE;

    private Remove()
    {
        commandName = "remove";
        arguments = "(id)";
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
        Integer id = Integer.valueOf(event.getMessage().getContentRaw().split(" ")[1]) - 1;
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.remove(id);
        System.out.println("ABOBA");
        Queue.getInstance().execute(event);
    }
}

