package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;



public class Join extends Command
{
    private static Join INSTANCE;

    private Join()
    {
        commandName = "join";
        description = "Добавить бота в голосовой чат";
    }

    public static Join getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Join();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        Member member = event.getMember();

        if (member.getVoiceState().inVoiceChannel())
        {
            final AudioManager audioManager = member.getGuild().getAudioManager();
            final VoiceChannel memberChannel = member.getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }
        
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.resume();
    }
}
