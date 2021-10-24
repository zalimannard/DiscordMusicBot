package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;


public class Resume extends Command
{
    private static Resume INSTANCE;

    private Resume()
    {
        commandName = "resume";
        description = "Продолжить проигрывание трека";
    }

    public static Resume getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Resume();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        Member member = event.getMember();
        if(member.getGuild().getAudioManager().isConnected())
        {
            PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.resume();
        }
    }
}

