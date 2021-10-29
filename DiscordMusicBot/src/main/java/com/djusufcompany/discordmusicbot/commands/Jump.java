package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;


public class Jump extends Command
{
    private static Jump INSTANCE;

    private Jump()
    {
        commandName = "jump";
        arguments = "(ID)";
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
        Member member = event.getMember();
        if (member.getVoiceState().inVoiceChannel())
        {
            final AudioManager audioManager = member.getGuild().getAudioManager();
            final VoiceChannel memberChannel = member.getVoiceState().getChannel();
            audioManager.openAudioConnection(memberChannel);
            
            Integer number = Integer.valueOf(event.getMessage().getContentRaw().substring(2 + commandName.length()));
            PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.jumpTo(number);
        }
    }
}

