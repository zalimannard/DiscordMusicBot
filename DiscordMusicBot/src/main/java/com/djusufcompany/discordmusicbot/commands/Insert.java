package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Insert extends Command
{
    private static Insert INSTANCE;

    private Insert()
    {
        commandName = "insert";
        arguments = "(ID) (URL)";
        description = "Вставить трек после указанного";
    }

    public static Insert getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Insert();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        if (event.getMember().getVoiceState().inVoiceChannel())
        {
            TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler;
            String message = event.getMessage().getContentRaw().substring(2 + commandName.length());
            String parts[] = message.split(" ");
            scheduler.insertTrack(Integer.valueOf(parts[0]), parts[1]);

            EmbedBuilder queueEmbed = new EmbedBuilder();
            queueEmbed.setColor(Color.decode("#2ECC71"));
            String trackName = scheduler.getTrackInfo(Integer.valueOf(parts[0])).getTitle();
            queueEmbed.setTitle("Трек \"" + trackName + "\" добавлен в очередь под номером " + (Integer.valueOf(parts[0]) + 1));
            event.getChannel().sendMessage(queueEmbed.build()).delay(20, TimeUnit.SECONDS).flatMap(Message::delete).submit();
        }
    }
}

