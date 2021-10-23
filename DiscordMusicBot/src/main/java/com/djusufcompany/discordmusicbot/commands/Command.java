package com.djusufcompany.discordmusicbot.commands;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public interface Command
{
    public void execute(MessageReceivedEvent event);
    
    public String getCommandName();

    public String getArguments();

    public String getDescription();
}

