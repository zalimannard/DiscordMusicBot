package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Insert extends Command
{
    private static Insert INSTANCE;

    private Insert()
    {
        commandName = "insert";
        arguments = "(id) (url)";
        description = "Вставить трек после указанного";
    }

    public static Insert getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Insert();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        String message = event.getMessage().getContentRaw().substring(2 + commandName.length());
        String parts[] = message.split(" ");
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.insert(Integer.valueOf(parts[0]) - 1, parts[1]);
        Queue.getInstance().execute(event);
    }
}

