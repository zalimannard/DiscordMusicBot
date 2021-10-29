package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Clear extends Command
{
    private static Clear INSTANCE;

    private Clear()
    {
        commandName = "clear";
        description = "Очистить текущую очередь";
    }

    public static Clear getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Clear();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        if (event.getMember().getVoiceState().inVoiceChannel())
        {
            TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler;
            Integer playingTrackNumberBeforeDeleting = scheduler.getCurrentTrackNumber();
            Integer queueSizeBeforeDeleting = scheduler.getQueueSize();
            Integer downloadReserve = scheduler.getDownloadReserve();

            // 3 разных цикла для предотвращения закачки файлов
            // Слева от текущего
            for (int i = 1; i < playingTrackNumberBeforeDeleting; i += 1)
            {
                scheduler.remove(1);
            }
            // Справа от текущего + предзагруженых
            for (int i = playingTrackNumberBeforeDeleting + downloadReserve; i <= queueSizeBeforeDeleting; i += 1)
            {
                scheduler.remove(downloadReserve + 1);
            }
            // Предзагруженные
            Integer queueSizePastTwoDeleting = scheduler.getQueueSize();
            for (int i = queueSizePastTwoDeleting; i >= 1; i -= 1)
            {
                scheduler.remove(i);
            }

            EmbedBuilder queueEmbed = new EmbedBuilder();
            queueEmbed.setColor(Color.decode("#2ECC71"));
            queueEmbed.setTitle("Очередь очищена");
            event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
        }
    }
}

