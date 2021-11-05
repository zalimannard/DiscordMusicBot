package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import com.djusufcompany.discordmusicbot.TrackScheduler;
import com.djusufcompany.discordmusicbot.Video;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;


public class Insert extends Command
{
    private static Insert INSTANCE;

    private Insert()
    {
        commandName = "insert";
        arguments = "(ID) (URL) / (ID) (запрос)";
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
            if (message.contains("https://"))
            {
                String parts[] = message.split(" ");
                scheduler.insertTrack(Integer.valueOf(parts[0]), Video.urlToId(parts[1]), true);
            }
            else
            {
                String keyword = message.substring(message.indexOf(" ") + 1);
                String url = "https://www.googleapis.com/youtube/v3/search?maxResults=1&q=" + keyword + "&key=AIzaSyANMWEq-XP4vTyPEQFvr9xujOjwikizkIc";
                String getJson = null;
                try
                {
                    getJson = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get().text();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(Insert.class.getName()).log(Level.SEVERE, null, ex);
                }

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

                String parts[] = message.split(" ");
                scheduler.insertTrack(Integer.valueOf(parts[0]), videoId, true);
            }
        }
    }
}

