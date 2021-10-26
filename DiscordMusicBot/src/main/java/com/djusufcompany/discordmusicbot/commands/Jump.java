package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Jump extends Command
{
    private static Jump INSTANCE;

    private Jump()
    {
        commandName = "jump";
        arguments = "(id)";
        description = "Переход к указанному в очереди";
    }

    public static Jump getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Jump();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        Integer number = Integer.valueOf(event.getMessage().getContentRaw().substring(2 + commandName.length()));
        PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.jump(number - 1);
    }
}

