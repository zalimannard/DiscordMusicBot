package com.djusufcompany.discordmusicbot.commands;


import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;


public abstract class Exit
{
    public static void execute(MessageReceivedEvent event)
    {
        event.getMember().getGuild().getAudioManager().closeAudioConnection();
    }
}

