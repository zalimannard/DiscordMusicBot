package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Loopq extends Command
{
    private static Loopq INSTANCE;

    private Loopq()
    {
        commandName = "loopq";
        description = "Вкл./Выкл. зацикливание очереди";
    }

    public static Loopq getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Loopq();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.changeQueueLoopMode();

        EmbedBuilder queueEmbed = new EmbedBuilder();
        queueEmbed.setColor(Color.decode("#2ECC71"));
        if (PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.getIsQueueLooped())
        {
            queueEmbed.setTitle("Повторение очереди включено");
        }
        else
        {
            queueEmbed.setTitle("Повторение очереди выключено");
        }
        event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
    }
}

