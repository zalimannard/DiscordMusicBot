package com.djusufcompany.discordmusicbot.commands;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Loop extends CommandData implements Command
{
    private static Loop INSTANCE;

    private Loop()
    {
        commandName = "loop";
        description = "Зацикливание текущего трека";
    }

    public static Loop getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Loop();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        //TODO
    }
}

