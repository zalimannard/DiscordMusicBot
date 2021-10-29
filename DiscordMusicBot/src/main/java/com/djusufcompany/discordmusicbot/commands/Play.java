package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import com.djusufcompany.discordmusicbot.Video;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;


public class Play extends Command
{
    private static Play INSTANCE;

    private Play()
    {
        commandName = "play";
        arguments = "(URL)";
        description = "Добавление трека/плейлиста в очередь по ссылке/запросу (Запроса пока нет (Это временно (Потом будет)))";
    }

    public static Play getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Play();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        Member member = event.getMember();
        String trackUrl = event.getMessage().getContentRaw().split(" ")[1];
        Info.getInstance().setChannel(event.getMessage().getChannel());

        if (member.getVoiceState().inVoiceChannel())
        {
            TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(member.getGuild()).scheduler;
            final AudioManager audioManager = member.getGuild().getAudioManager();
            final VoiceChannel memberChannel = member.getVoiceState().getChannel();
            EmbedBuilder queueEmbed = new EmbedBuilder();
            queueEmbed.setColor(Color.decode("#2ECC71"));

            if (trackUrl.contains("/playlist?list="))
            {
                audioManager.openAudioConnection(memberChannel);
                ArrayList<String> urls = Video.getTracksUrlFromPlaylist(trackUrl);
                for (String url : urls)
                {
                    try
                    {
                        scheduler.insertTrack(scheduler.getQueueSize(), url);
                        scheduler.resume();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Не получилось добавить трек из плейлиста");
                    }
                }

                queueEmbed.setTitle("Плейлист добавлен в очередь");
                event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
            }
            else if (trackUrl.contains("https://"))
            {
                audioManager.openAudioConnection(memberChannel);
                scheduler.insertTrack(scheduler.getQueueSize(), trackUrl);
                scheduler.resume();

                String trackName = scheduler.getTrackInfo(scheduler.getQueueSize()).getTitle();
                queueEmbed.setTitle("Трек \"" + trackName + "\" добавлен в очередь");
                event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
            }
            else
            {
                audioManager.openAudioConnection(memberChannel);
                // ЗДЕСЬ БУДЕТ ПОИСК
            }
        }
    }
}

