package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
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
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler;
        Member member = event.getMember();
        String trackUrl = "https://youtu.be/7-JnqyEpEXM";
        Info.getInstance().setChannel(event.getMessage().getChannel());

        if (member.getVoiceState().inVoiceChannel())
        {
            final AudioManager audioManager = member.getGuild().getAudioManager();
            final VoiceChannel memberChannel = member.getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
            scheduler.insertTrack(scheduler.getQueueSize(), trackUrl);
            scheduler.resume();

            
            EmbedBuilder queueEmbed = new EmbedBuilder();
            queueEmbed.setColor(Color.decode("#2ECC71"));
            String trackName = scheduler.getTrackInfo(scheduler.getQueueSize()).getTitle();
            queueEmbed.setTitle("Трек \"" + trackName + "\" добавлен в очередь");
            event.getChannel().sendMessage(queueEmbed.build()).delay(20, TimeUnit.SECONDS).flatMap(Message::delete).submit();
        }
    }
}

