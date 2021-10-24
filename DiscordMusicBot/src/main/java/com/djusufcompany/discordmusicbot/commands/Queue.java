package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackInfo;
import java.awt.Color;
import java.util.ArrayList;
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
        ArrayList<TrackInfo> queueInfo = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.getQueueInfo();
        EmbedBuilder queueEmbed = new EmbedBuilder();

        queueEmbed.setColor(Color.decode("#2ECC71"));
        queueEmbed.setTitle("Очередь воспроизведения:");

        for (int i = 0; i < queueInfo.size(); i += 1)
        {
            queueEmbed.addField((i + 1) + ". " + queueInfo.get(i).getTitle(),
                    queueInfo.get(i).getAuthor() + "\n" + queueInfo.get(i).getUrl(), false);
        }

        event.getChannel().sendMessage(queueEmbed.build()).queue();
    }
}

