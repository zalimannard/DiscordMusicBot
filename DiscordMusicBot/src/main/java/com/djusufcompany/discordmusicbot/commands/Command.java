package com.djusufcompany.discordmusicbot.commands;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public abstract class Command
{
    protected String commandName = "";
    protected String arguments = "";
    protected String description = "";

    public String getCommandName()
    {
        return commandName;
    }

    public String getArguments()
    {
        return arguments;
    }

    public String getDescription()
    {
        return description;
    }

    public abstract void execute(MessageReceivedEvent event);
}

