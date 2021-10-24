package com.djusufcompany.discordmusicbot;


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
    private Integer downloadReserve = Integer.valueOf(ConfProperties.getProperty("downloadReserve"));
    private Boolean isTrackLooped = false;
    private Boolean isQueueLooped = false;
    private File outputDir = null;

    public TrackScheduler(AudioPlayer player, Guild guild)
    {
        this.player = player;
        this.guild = guild;
        this.queue = new ArrayList<>();
        this.downloaded = new ArrayList<>();
    }

    public void addToQueue(String url)
    {
        YoutubeDownloader downloader = new YoutubeDownloader();
        VideoInfo video = getVideoInfo(downloader, url);

        queue.add(new TrackInfo(video.details().author(), video.details().title(), url));

        if (downloaded.size() < downloadReserve)
        {
            downloaded.add(loadTrackFromUrl(url));
        }
        if (downloaded.size() == 1)
        {
            nextTrack();
        }
    }

    public void play(AudioTrack track)
    {
        player.startTrack(track, true);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if (isTrackLooped)
        {
            playTopTrack();
        }
        else
        {
            currentTrackNumber += 1;
            downloaded.get(0).data().delete();
            downloaded.remove(0);
            nextTrack();
        }
    }

    public void nextTrack()
    {
        if (currentTrackNumber + downloaded.size() < queue.size())
        {
            Response<File> loadTrackFromUrl = loadTrackFromUrl(queue.get(currentTrackNumber + downloadReserve - 1).getUrl());
            downloaded.add(loadTrackFromUrl);
        }

        if (downloaded.size() == 0)
        {
            if ((isQueueLooped) && (queue.size() > 0))
            {
                ArrayList<TrackInfo> tmpQueue = (ArrayList<TrackInfo>) queue.clone();
                Integer size = PlayerManager.getInstance().getMusicManager(guild).scheduler.getQueueInfo().size();
                for (int i = 0; i < size; i += 1)
                {
                    PlayerManager.getInstance().getMusicManager(guild).scheduler.remove(0);
                }
                for (int i = 0; i < tmpQueue.size(); i += 1)
                {
                    addToQueue(tmpQueue.get(i).getUrl());
                }
            }
            else
            {
                guild.getAudioManager().closeAudioConnection();
            }
        }
        else
        {
            playTopTrack();
        }
    }

    public TrackInfo getTrackInfo()
    {
        return queue.get(currentTrackNumber);
    }

    public void playTopTrack()
    {
        PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
    }

    public ArrayList<TrackInfo> getQueueInfo()
    {
        return queue;
    }

    public void skip()
    {
        player.stopTrack();
        nextTrack();
    }

    public void remove(Integer id)
    {
        if ((id >= 0) && (id < queue.size()))
        {
            queue.remove(queue.get(id));
            if (id == currentTrackNumber)
            {
                System.out.println("Тип удаления 1");
                downloaded.get(0).data().delete();
                downloaded.remove(0);
                skip();
            }
            else if (id < currentTrackNumber)
            {
                currentTrackNumber -= 1;
            }
            else if (currentTrackNumber + downloadReserve > id)
            {
                downloaded.get(id - currentTrackNumber).data().delete();
                downloaded.remove(id - currentTrackNumber);
                if (currentTrackNumber + downloadReserve - 1 < queue.size())
                {
                    downloaded.add(loadTrackFromUrl(queue.get(currentTrackNumber + downloadReserve - 1).getUrl()));
                }
            }
        }
    }

    private Response<File> loadTrackFromUrl(String url)
    {
        YoutubeDownloader downloader = new YoutubeDownloader();
        VideoInfo video = getVideoInfo(downloader, url);

        Format format = video.bestAudioFormat();
        if (format == null)
        {
            format = video.bestVideoFormat();
        }
        if (format != null)
        {
            if (outputDir == null)
            {
                createOutputDir();
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

        String[] tagArray =
        {
            "&t=",
            "&t=",
            "&list="
        };
        String[] urlTypeArray =
        {
            "https://www.youtube.com/watch?v=",
            "https://youtu.be/"
        };

        for (String tag : tagArray)
        {
            if (url.contains(tag))
            {
                url = url.substring(0, url.indexOf(tag));
            }
        }

        for (String urlType : urlTypeArray)
        {
            if (url.contains(urlType))
            {
                videoId = url.substring(urlType.length());
            }
        }

        return videoId;
    }

    public void changeTrackLoopMode()
    {
        isTrackLooped = !isTrackLooped;
    }

    public void changeQueueLoopMode()
    {
        isQueueLooped = !isQueueLooped;
    }

    public VideoInfo getVideoInfo(YoutubeDownloader downloader, String url)
    {
        String videoId = urlToId(url);
        RequestVideoInfo requestInfo = new RequestVideoInfo(videoId);
        Response<VideoInfo> responseInfo = downloader.getVideoInfo(requestInfo);
        return responseInfo.data();
    }

    public void createOutputDir()
    {
        try
        {
            outputDir = Files.createTempDirectory("video").toFile();

        }
        catch (IOException ex)
        {
            Logger.getLogger(TrackScheduler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}

