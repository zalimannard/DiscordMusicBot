package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackInfo;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
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
        ArrayList<TrackInfo> queueInfo = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.getQueueInfo();
        Integer currentTrackNumber = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.getCurentTrackNumber() + 1;
        for (int i = 0; i < queueInfo.size() / 25 + 1; i += 1)
        {
            EmbedBuilder queueEmbed = new EmbedBuilder();
            queueEmbed.setColor(Color.decode("#2ECC71"));
            queueEmbed.setTitle("Очередь воспроизведения:");

            for (int j = 0; j < Math.min(queueInfo.size() - i * 25, 25); j += 1)
            {
                if (currentTrackNumber == (i * 25) + j + 1)
                {
                    queueEmbed.addField((">>>>> " + (i * 25) + j + 1) + ". " + queueInfo.get((i * 25) + j).getTitle(),
                            queueInfo.get((i * 25) + j).getAuthor() + "\n" + queueInfo.get((i * 25) + j).getUrl(), false);
                }
                else
                {
                    queueEmbed.addField(((i * 25) + j + 1) + ". " + queueInfo.get((i * 25) + j).getTitle(),
                            queueInfo.get((i * 25) + j).getAuthor() + "\n" + queueInfo.get((i * 25) + j).getUrl(), false);
                }
            }

            event.getChannel().sendMessage(queueEmbed.build()).delay(180, TimeUnit.SECONDS).flatMap(Message::delete).submit();
        }
    }
}

