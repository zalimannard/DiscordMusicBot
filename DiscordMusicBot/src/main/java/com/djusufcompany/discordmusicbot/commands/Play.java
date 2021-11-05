package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import com.djusufcompany.discordmusicbot.Video;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;


public class Play extends Command
{
    private static Play INSTANCE;

    private Play()
    {
        commandName = "play";
        arguments = "(URL) / (запрос)";
        description = "Добавление трека/плейлиста в очередь по ссылке/запросу";
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
        String videoUrl = event.getMessage().getContentRaw().split(" ")[1];

        if (member.getVoiceState().inVoiceChannel())
        {
            TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(member.getGuild()).scheduler;
            final AudioManager audioManager = member.getGuild().getAudioManager();
            final VoiceChannel memberChannel = member.getVoiceState().getChannel();

            if (videoUrl.contains("/playlist?list="))
            {
                audioManager.openAudioConnection(memberChannel);
                ArrayList<String> ids = Video.getTracksVideoIdFromPlaylist(videoUrl);
                for (String id : ids)
                {
                    try
                    {
                        scheduler.insertTrack(scheduler.getQueueSize(), id, false);
                        scheduler.resume();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Не получилось добавить трек из плейлиста");
                    }
                }
                Queue.getInstance().execute(event);
            }
            else if (videoUrl.contains("https://"))
            {
                audioManager.openAudioConnection(memberChannel);
                String videoId = Video.urlToId(videoUrl);
                scheduler.insertTrack(scheduler.getQueueSize(), videoId, true);
                scheduler.resume();
            }
            else
            {
                try
                {
                    audioManager.openAudioConnection(memberChannel);

                    String keyword = event.getMessage().getContentRaw().substring(commandName.length() + 2);
                    String url = "https://www.googleapis.com/youtube/v3/search?maxResults=1&q=" + keyword + "&key=AIzaSyANMWEq-XP4vTyPEQFvr9xujOjwikizkIc";
                    String getJson = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get().text();

                    JSONArray jsonItem;
                    JSONObject item;
                    JSONObject id;
                    String videoId = null;

                    try
                    {
                        jsonItem = new JSONObject(getJson).getJSONArray("items");
                        item = jsonItem.getJSONObject(0);
                        id = item.getJSONObject("id");
                        videoId = id.getString("videoId");
                    }
                    catch (JSONException ex)
                    {
                        Logger.getLogger(Play.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    audioManager.openAudioConnection(memberChannel);
                    scheduler.insertTrack(scheduler.getQueueSize(), videoId, true);
                    scheduler.resume();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(Play.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

