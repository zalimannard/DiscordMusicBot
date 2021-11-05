package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackInfo;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Queue extends Command
{
    private static Queue INSTANCE;

    private Queue()
    {
        commandName = "queue";
        description = "Печать текущей очереди";
    }

    public static Queue getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Queue();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler;
        Integer currentTrackNumber = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.getCurrentTrackNumber();
        Integer queueSize = scheduler.getQueueSize();
        EmbedBuilder queueEmbed = new EmbedBuilder();
        queueEmbed.setColor(Color.decode("#2ECC71"));
        queueEmbed.setTitle("Очередь воспроизведения:");
        for (int i = 1; i <= queueSize; i += 1)
        {
            TrackInfo regularTrack = scheduler.getTrackInfo(i);

            if (currentTrackNumber == i)
            {
                queueEmbed.addField(">>>>> " + i + ". " + regularTrack.getTitle(),
                        regularTrack.getAuthor() + "\nhttps://youtu.be/" + regularTrack.getVideoId(), false);
            }
            else
            {
                queueEmbed.addField(i + ". " + regularTrack.getTitle(),
                        regularTrack.getAuthor() + "\nhttps://youtu.be/" + regularTrack.getVideoId(), false);
            }

            if ((i % 25 == 0) || (i == queueSize))
            {
                event.getChannel().sendMessage(queueEmbed.build()).submit();
                queueEmbed.clear();
                queueEmbed.setColor(Color.decode("#2ECC71"));
                queueEmbed.setTitle("Очередь воспроизведения:");
            }
        }
    }
}

