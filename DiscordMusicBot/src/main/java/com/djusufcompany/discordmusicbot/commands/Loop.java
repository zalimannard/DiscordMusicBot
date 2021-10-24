package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
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
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.changeTrackLoopMode();
        
        EmbedBuilder queueEmbed = new EmbedBuilder();
        queueEmbed.setColor(Color.decode("#2ECC71"));
        if (PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.getIsTrackLooped())
        {
            queueEmbed.setTitle("Повторение трека включено");
        }
        else
        {
            queueEmbed.setTitle("Повторение трека выключено");
        }
        event.getChannel().sendMessage(queueEmbed.build()).queue();
    }
}

