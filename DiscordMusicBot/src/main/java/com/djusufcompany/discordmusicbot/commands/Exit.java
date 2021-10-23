package com.djusufcompany.discordmusicbot.commands;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Exit extends CommandData implements Command
{
    private static Exit INSTANCE;

    private Exit()
    {
        commandName = "exit";
        description = "Отключение бота от канала";
    }

    public static Exit getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Exit();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        event.getMember().getGuild().getAudioManager().closeAudioConnection();
    }
}

