package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackInfo;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import java.util.ArrayList;
import java.util.Random;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Shuffle extends Command
{
    private static Shuffle INSTANCE;

    private Shuffle()
    {
        commandName = "shuffle";
        description = "Перемешать очередь";
    }

    public static Shuffle getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Shuffle();
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

            ArrayList<TrackInfo> tmpQueue = new ArrayList<>();
            for (int i = 1; i <= queueSizeBeforeDeleting; i++)
            {
                if (playingTrackNumberBeforeDeleting != i)
                {
                    tmpQueue.add(scheduler.getTrackInfo(i));
                }
            }

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
            for (int i = queueSizePastTwoDeleting; i > 1; i -= 1)
            {
                scheduler.remove(i);
            }
            // Возврат
            Random random = new Random();
            while (tmpQueue.size() > 0)
            {
                Integer number = random.nextInt(tmpQueue.size());
                scheduler.insertTrack(scheduler.getQueueSize(), tmpQueue.get(number).getVideoId(), false);
                tmpQueue.remove(tmpQueue.get(number));
            }

            Queue.getInstance().execute(event);
        }
    }
}

