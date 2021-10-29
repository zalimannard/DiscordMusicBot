package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackInfo;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Info extends Command
{
    private static Info INSTANCE;
    private static MessageChannel channel;

    private Info()
    {
        commandName = "info";
        description = "Описание текущего трека";
    }

    public static Info getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Info();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler;
        TrackInfo nowPlayingInfo = scheduler.getTrackInfo(scheduler.getCurrentTrackNumber());
        
        EmbedBuilder nowPlayingEmbed = new EmbedBuilder();
        nowPlayingEmbed.setColor(Color.decode("#2ECC71"));
        nowPlayingEmbed.setTitle("Сейчас играет:");

        nowPlayingEmbed.addField(nowPlayingInfo.getTitle(),
                nowPlayingInfo.getAuthor() + "\n"
                + nowPlayingInfo.getUrl() + "\n"
                + scheduler.getTrackTime(), false);
        
        channel.sendMessage(nowPlayingEmbed.build()).submit();
    }
    
    public void printNowPlaying(TrackInfo info)
    {
        EmbedBuilder nowPlayingEmbed = new EmbedBuilder();
        nowPlayingEmbed.setColor(Color.decode("#2ECC71"));
        nowPlayingEmbed.setTitle("Сейчас играет:");

        nowPlayingEmbed.addField(info.getTitle(),
                info.getAuthor() + "\n"
                + info.getUrl(), false);
        
        channel.sendMessage(nowPlayingEmbed.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).submit();
    }
    
    public void setChannel(MessageChannel channel)
    {
        this.channel = channel;
    }
}

