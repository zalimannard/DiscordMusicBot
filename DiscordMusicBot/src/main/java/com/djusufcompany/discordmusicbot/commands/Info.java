package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.Time;
import com.djusufcompany.discordmusicbot.TrackInfo;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Info extends Command
{
    private static Info INSTANCE;
    private static MessageChannel channel;
    private static Message nowPlayingMessage;

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
                + "https://www.youtube.com/watch?v=" + nowPlayingInfo.getVideoId() + "\n"
                + scheduler.getCurrentTrackTimeHms(), false);
        nowPlayingEmbed.setThumbnail("https://i.ytimg.com/vi/" + nowPlayingInfo.getVideoId() + "/default.jpg");

        channel.sendMessage(nowPlayingEmbed.build()).submit();
    }

    public void printNowPlaying(TrackInfo info)
    {
        EmbedBuilder nowPlayingEmbed = new EmbedBuilder();
        nowPlayingEmbed.setColor(Color.decode("#2ECC71"));
        nowPlayingEmbed.setTitle("Сейчас играет:");

        nowPlayingEmbed.addField(info.getTitle(),
                info.getAuthor() + "\n"
                + "https://www.youtube.com/watch?v=" + info.getVideoId() + "\n"
                + "Продолжительность: " + Time.timeValueToStringHms(info.getDuration()), false);
        nowPlayingEmbed.setImage("https://i.ytimg.com/vi/" + info.getVideoId() + "/hqdefault.jpg");

        try
        {
            deleteNowPlaying();
            nowPlayingMessage = channel.sendMessage(nowPlayingEmbed.build()).submit().get();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Info.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ExecutionException ex)
        {
            Logger.getLogger(Info.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printTrackAdded(TrackInfo info)
    {
        EmbedBuilder nowPlayingEmbed = new EmbedBuilder();
        nowPlayingEmbed.setColor(Color.decode("#2ECC71"));
        nowPlayingEmbed.setTitle("Трек добавлен:");

        nowPlayingEmbed.addField(info.getTitle(),
                info.getAuthor() + "\n"
                + "https://www.youtube.com/watch?v=" + info.getVideoId() + "\n"
                + "Продолжительность: " + Time.timeValueToStringHms(info.getDuration()), false);
        nowPlayingEmbed.setThumbnail("https://i.ytimg.com/vi/" + info.getVideoId() + "/default.jpg");
        channel.sendMessage(nowPlayingEmbed.build()).submit();
    }

    public void setChannel(MessageChannel channel)
    {
        if (this.channel == null)
        {
            this.channel = channel;
        }
        else if (!channel.getId().equals(this.channel.getId()))
        {
            deleteNowPlaying();
            nowPlayingMessage.delete();
            nowPlayingMessage = null;
            this.channel = channel;
        }
    }

    public static void deleteNowPlaying()
    {
        if ((channel != null) && (nowPlayingMessage != null))
        {
            nowPlayingMessage.delete().submit();
        }
    }
}

