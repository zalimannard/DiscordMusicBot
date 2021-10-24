package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;



public class Lasttrack extends Command
{
    private static Lasttrack INSTANCE;

    private Lasttrack()
    {
        commandName = "lasttrack";
        description = "Добавление в очередь DJ Yousuf \"Последний трек\"";
    }

    public static Lasttrack getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Lasttrack();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        Member member = event.getMember();
        String trackUrl = "https://youtu.be/7-JnqyEpEXM";

        if (member.getVoiceState().inVoiceChannel())
        {
            final AudioManager audioManager = member.getGuild().getAudioManager();
            final VoiceChannel memberChannel = member.getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
            PlayerManager.getInstance().getMusicManager(member.getGuild()).scheduler.addToQueue(trackUrl);
        }
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.resume();
    }
}

