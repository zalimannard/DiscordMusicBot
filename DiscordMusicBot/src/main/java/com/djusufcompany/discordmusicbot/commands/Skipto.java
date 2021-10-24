package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Skipto extends Command
{
    private static Skipto INSTANCE;

    private Skipto()
    {
        commandName = "skipto";
        arguments = "(id)";
        description = "Переход к указанному в очереди";
    }

    public static Skipto getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Skipto();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        Integer number = Integer.valueOf(event.getMessage().getContentRaw().substring(2 + commandName.length()));
        PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.skipTo(number - 1);
    }
}

