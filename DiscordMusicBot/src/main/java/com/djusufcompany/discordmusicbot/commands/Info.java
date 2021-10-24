package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackInfo;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Info extends Command
{
    private static Info INSTANCE;

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
        TrackInfo nowPlayingInfo = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.getTrackInfo();
        EmbedBuilder nowPlayingEmbed = new EmbedBuilder();

        nowPlayingEmbed.setColor(Color.decode("#2ECC71"));
        nowPlayingEmbed.setTitle("Сейчас играет:");

        nowPlayingEmbed.addField(nowPlayingInfo.getTitle(),
                nowPlayingInfo.getAuthor() + "\n" + nowPlayingInfo.getUrl(), false);

        event.getChannel().sendMessage(nowPlayingEmbed.build()).queue();
    }
}

