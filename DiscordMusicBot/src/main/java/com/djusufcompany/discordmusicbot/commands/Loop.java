package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Loop extends Command
{
    private static Loop INSTANCE;

    private Loop()
    {
        commandName = "loop";
        description = "Вкл./Выкл. зацикливание трека";
    }

    public static Loop getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Loop();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        if (event.getMember().getVoiceState().inVoiceChannel())
        {
            TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler;
            scheduler.changeTrackLoopMode();

            EmbedBuilder queueEmbed = new EmbedBuilder();
            queueEmbed.setColor(Color.decode("#2ECC71"));
            if (scheduler.isTrackLooped())
            {
                queueEmbed.setTitle("Повторение трека включено");
            }
            else
            {
                queueEmbed.setTitle("Повторение трека выключено");
            }
            event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
        }
    }
}

