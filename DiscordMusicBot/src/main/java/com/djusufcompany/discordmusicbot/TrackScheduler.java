package com.djusufcompany.discordmusicbot;


import com.github.kiulian.downloader.Config;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Guild;


public class TrackScheduler extends AudioEventAdapter
{
    private final AudioPlayer player;
    private final Guild guild;
    private ArrayList<TrackInfo> queue;
    private ArrayList<Response<File>> downloaded;
    private Integer currentTrackNumber = 0;
    private Integer downloadBuferNumber = 3;

    public TrackScheduler(AudioPlayer player, Guild guild)
    {
        this.player = player;
        this.guild = guild;
        this.queue = new ArrayList<>();
        this.downloaded = new ArrayList<>();
    }

    public void addToQueue(String tracksUrl)
    {
        YoutubeDownloader downloader = new YoutubeDownloader();
        String videoId = urlToId(tracksUrl);
        RequestVideoInfo requestInfo = new RequestVideoInfo(videoId);
        Response<VideoInfo> responseInfo = downloader.getVideoInfo(requestInfo);
        VideoInfo video = responseInfo.data();
        
        queue.add(new TrackInfo(video.details().author(), video.details().title(), tracksUrl));

        if (downloaded.size() < downloadBuferNumber)
        {
            downloaded.add(loadTrackFromUrl(tracksUrl));
        }
        if (downloaded.size() == 1)
        {
            PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
        }
    }

    public void play(AudioTrack track)
    {
        player.startTrack(track, true);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if (endReason.mayStartNext)
        {
            nextTrack();
        }
        currentTrackNumber += 1;
    }

    public void nextTrack()
    {
        if ((downloaded.size() >= downloadBuferNumber) && (queue.size() - currentTrackNumber > downloadBuferNumber))
        {
            downloaded.add(loadTrackFromUrl(queue.get(currentTrackNumber + downloadBuferNumber).getUrl()));
        }
        downloaded.get(0).data().delete();
        downloaded.remove(0);
        if (downloaded.size() == 0)
        {
            guild.getAudioManager().closeAudioConnection();
        }
        else
        {
            PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
        }
    }

    public TrackInfo getTrackInfo()
    {
        return queue.get(currentTrackNumber);
    }

    public ArrayList<TrackInfo> getQueueInfo()
    {
        return queue;
    }

    public void skip()
    {
        if (currentTrackNumber + downloadBuferNumber - 1 < queue.size())
        {
            downloaded.add(loadTrackFromUrl(queue.get(currentTrackNumber + downloadBuferNumber).getUrl()));
        }
        downloaded.get(0).data().delete();
        downloaded.remove(0);
        player.stopTrack();
        if (downloaded.size() == 0)
        {
            guild.getAudioManager().closeAudioConnection();
        }
        else
        {
            PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
        }
    }

    public void remove(Integer id)
    {
        if ((id >= queue.size()) || (id < 0))
        {
            System.out.println("1");
            return;
        }
        queue.remove(queue.get(id));
        if (id == currentTrackNumber)
        {
            System.out.println("2");
            currentTrackNumber -= 1;
            skip();
        }
        else if (id < currentTrackNumber)
        {
            System.out.println("3");
            currentTrackNumber -= 1;
        }
        else if (currentTrackNumber + downloadBuferNumber > id)
        {
            System.out.println("4");
            downloaded.get(id - currentTrackNumber).data().delete();
            downloaded.remove(id - currentTrackNumber);
            if (currentTrackNumber + downloadBuferNumber - 1 < queue.size())
            {
                downloaded.add(loadTrackFromUrl(queue.get(currentTrackNumber + downloadBuferNumber).getUrl()));
            }
        }
    }

    private Response<File> loadTrackFromUrl(String url)
    {
        YoutubeDownloader downloader = new YoutubeDownloader();
        Config config = downloader.getConfig();
        config.setMaxRetries(0);
        String videoId = urlToId(url);

        RequestVideoInfo requestInfo = new RequestVideoInfo(videoId);
        Response<VideoInfo> responseInfo = downloader.getVideoInfo(requestInfo);
        VideoInfo video = responseInfo.data();

        Format format = video.bestAudioFormat();
        if (format == null)
        {
            format = video.bestVideoFormat();
        }
        if (format != null)
        {
            File outputDir = null;
            try
            {
                outputDir = Files.createTempDirectory("video").toFile();
            }
            catch (IOException ex)
            {
                Logger.getLogger(TrackScheduler.class.getName()).log(Level.SEVERE, null, ex);
            }
            RequestVideoFileDownload requestFile = new RequestVideoFileDownload(format)
                    .saveTo(outputDir)
                    .renameTo("video");
            return downloader.downloadVideoFile(requestFile);
        }
        return null;
    }

    private String urlToId(String url)
    {
        String videoId = null;
        if (url.contains("&t="))
        {
            url = url.substring(0, url.indexOf("&t="));
        }
        else if (url.contains("?t="))
        {
            url = url.substring(0, url.indexOf("?t="));
        }
        if (url.contains("&list="))
        {
            url = url.substring(0, url.indexOf("&list="));
        }

        if (url.contains("https://www.youtube.com/watch?v="))
        {
            videoId = url.substring("https://www.youtube.com/watch?v=".length());
        }
        else if (url.contains("https://youtu.be/"))
        {
            videoId = url.substring("https://youtu.be/".length());
        }
        return videoId;
    }
}

