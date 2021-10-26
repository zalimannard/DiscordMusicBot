package com.djusufcompany.discordmusicbot;


import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestPlaylistInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.playlist.PlaylistInfo;
import com.github.kiulian.downloader.model.playlist.PlaylistVideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;


public class TrackScheduler extends AudioEventAdapter
{
    private final AudioPlayer player;
    private final Guild guild;
    private ArrayList<TrackInfo> queue;
    private ArrayList<Response<File>> downloaded;
    private Integer currentTrackNumber = 0;
    private Integer downloadReserve = 3;
    private Boolean isTrackLooped = false;
    private Boolean isQueueLooped = false;
    private File outputDir = null;
    private Boolean fromLoadTrack = false;
    private Boolean endOfTrack = false;
    TextChannel textChannel = null;

    public TrackScheduler(AudioPlayer player, Guild guild)
    {
        this.player = player;
        this.guild = guild;
        this.queue = new ArrayList<>();
        this.downloaded = new ArrayList<>();

        try
        {
            this.outputDir = Files.createTempDirectory("video").toFile();
        }
        catch (IOException ex)
        {
            Logger.getLogger(TrackScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTrackToQueue(String url, TextChannel newTextChannel)
    {
        textChannel = newTextChannel;

        YoutubeDownloader downloader = new YoutubeDownloader();
        VideoInfo video = Video.getVideoInfo(downloader, url);

        queue.add(new TrackInfo(video.details().author(), video.details().title(), url));

        if (downloaded.size() < downloadReserve)
        {
            downloaded.add(Video.loadTrackFromUrl(outputDir, url));
        }
        if (downloaded.size() == 1)
        {
            loadTrack(queue.size() - 1);
        }
    }

    public void addPlaylistToQueue(String url, TextChannel newTextChannel)
    {
        YoutubeDownloader downloader = new YoutubeDownloader();
        String playlistId = url.substring("https://www.youtube.com/playlist?list=".length());
        RequestPlaylistInfo request = new RequestPlaylistInfo(playlistId);
        Response<PlaylistInfo> response = downloader.getPlaylistInfo(request);
        PlaylistInfo playlistInfo = response.data();
        List<PlaylistVideoDetails> tracks = playlistInfo.videos();
        for (PlaylistVideoDetails track : tracks)
        {
            addTrackToQueue("https://www.youtube.com/watch?v=" + track.videoId(), newTextChannel);
        }
    }

    public void play(AudioTrack track)
    {
        player.startTrack(track, true);
    }

    public void skip()
    {
        if ((isQueueLooped) && (currentTrackNumber + 1 == queue.size()))
        {
            loadTrack(0);
        }
        else if (currentTrackNumber + 1 == queue.size())
        {
            player.stopTrack();
        }
        else
        {
            loadTrack(currentTrackNumber + 1);
        }
    }

    public void remove(Integer id)
    {
        if ((id >= 0) && (id < queue.size()))
        {
            queue.remove(queue.get(id));
            if (id == currentTrackNumber)
            {
                downloaded.get(0).data().delete();
                downloaded.remove(0);
                if (downloaded.size() > 0)
                {
                    loadTrack(currentTrackNumber);
                }
                else if ((isQueueLooped) && (queue.size() > 0))
                {
                    loadTrack(0);
                }
                else
                {
                    endOfTrack = true;
                    player.stopTrack();
                    guild.getAudioManager().closeAudioConnection();
                }
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
                    downloaded.add(Video.loadTrackFromUrl(outputDir, queue.get(currentTrackNumber + downloadReserve - 1).getUrl()));
                }
            }
        }
    }

    public void clear()
    {
        // 3 разных цикла для предотвращения закачки файлов
        // Слева от текущего
        for (int i = 0; i < currentTrackNumber; i += 1)
        {
            remove(0);
        }
        // Справа от текущего + предзагруженых
        Integer queueSize = queue.size();
        for (int i = downloadReserve; i < queueSize; i += 1)
        {
            remove(downloadReserve);
        }
        // Предзагруженные
        queueSize = queue.size();
        for (int i = queueSize - 1; i >= 0; i -= 1)
        {
            remove(i);
        }
    }

    public void shuffle()
    {
        ArrayList<TrackInfo> tmpQueue = (ArrayList<TrackInfo>) queue.clone();
        tmpQueue.remove(tmpQueue.get(currentTrackNumber));
        // 3 разных цикла для предотвращения закачки файлов
        // Слева от текущего
        for (int i = 0; i < currentTrackNumber; i += 1)
        {
            queue.remove(queue.get(0));
        }
        // Справа от текущего + предзагруженых
        Integer queueSize = queue.size();
        for (int i = downloadReserve; i < queueSize; i += 1)
        {
            queue.remove(queue.get(downloadReserve));
        }
        // Предзагруженные
        queueSize = queue.size();
        for (int i = 1; i < queueSize; i += 1)
        {
            queue.remove(queue.get(1));
            downloaded.get(1).data().delete();
            downloaded.remove(1);
        }
        Random random = new Random();
        while (tmpQueue.size() > 0)
        {
            Integer number = random.nextInt(tmpQueue.size());
            addTrackToQueue(tmpQueue.get(number).getUrl(), textChannel);
            tmpQueue.remove(tmpQueue.get(number));
        }
        currentTrackNumber = 0;
    }

    public void changeTrackLoopMode()
    {
        isTrackLooped = !isTrackLooped;
    }

    public void changeQueueLoopMode()
    {
        isQueueLooped = !isQueueLooped;
    }

    public void previousTrack()
    {
        if (currentTrackNumber > 0)
        {
            loadTrack(currentTrackNumber - 1);
        }
    }

    public void jump(Integer id)
    {
        loadTrack(id);
    }

    public void insert(Integer id, String url)
    {
        if ((id >= 0) && (id < queue.size()))
        {
            if ((id - currentTrackNumber < downloadReserve) && (currentTrackNumber < id))
            {
                while (downloaded.size() > 1)
                {
                    downloaded.get(1).data().delete();
                    downloaded.remove(1);
                }
            }

            YoutubeDownloader downloader = new YoutubeDownloader();
            VideoInfo video = Video.getVideoInfo(downloader, url);
            queue.add(id + 1, new TrackInfo(video.details().author(), video.details().title(), url));

            if (id < currentTrackNumber)
            {
                currentTrackNumber += 1;
            }
            if ((id - currentTrackNumber < downloadReserve) && (currentTrackNumber < id))
            {
                for (int i = currentTrackNumber + 1; i < Math.min(queue.size(), currentTrackNumber + downloadReserve); i += 1)
                {
                    downloaded.add(Video.loadTrackFromUrl(outputDir, queue.get(i).getUrl()));
                }
            }
        }
    }

    public void pause()
    {
        player.setPaused(true);
    }

    public void resume()
    {
        player.setPaused(false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if (fromLoadTrack)
        {
            fromLoadTrack = false;
        }
        else
        {
            endOfTrack = true;
            if (isTrackLooped)
            {
                loadTrack(currentTrackNumber);
            }
            else if ((isQueueLooped) && (currentTrackNumber + 1 == queue.size()))
            {
                loadTrack(0);
            }
            else if (currentTrackNumber + 1 == queue.size())
            {
                guild.getAudioManager().closeAudioConnection();
            }
            else
            {
                loadTrack(currentTrackNumber + 1);
            }
            endOfTrack = false;
        }
    }

    public TrackInfo getTrackInfo(String code)
    {
        if (code.equals("New"))
        {
            return queue.get(queue.size() - 1);
        }
        else if (code.equals("Current"))
        {
            return queue.get(currentTrackNumber);
        }
        else
        {
            return queue.get(currentTrackNumber);
        }
    }

    public void setTrackTime(String time)
    {
        String[] hms = time.split(".");
        Long newTimeInLong = 0L;

        if (hms.length == 1)
        {
            newTimeInLong += Long.valueOf(hms[0]) * 1000;
        }
        else if (hms.length == 2)
        {
            newTimeInLong += 0
                    + Long.valueOf(hms[0]) * 60 * 1000
                    + Long.valueOf(hms[1]) * 1000;
        }
        else if (hms.length == 3)
        {
            newTimeInLong += 0
                    + Long.valueOf(hms[0]) * 60 * 60 * 1000
                    + Long.valueOf(hms[1]) * 60 * 1000
                    + Long.valueOf(hms[2]) * 1000;
        }

        Long trackLength = player.getPlayingTrack().getInfo().length;
        if ((newTimeInLong >= 0) && (newTimeInLong < trackLength))
        {
            player.getPlayingTrack().setPosition(newTimeInLong);
        }
    }

    public String getTrackTime()
    {
        Long currentPoint = player.getPlayingTrack().getPosition();
        currentPoint /= 1000;

        String currentPointSeconds = String.valueOf(currentPoint % 60);
        currentPoint /= 60;
        if (currentPointSeconds.length() == 1)
        {
            currentPointSeconds = "0" + currentPointSeconds;
        }
        String currentPointMinutes = String.valueOf(currentPoint % 60);
        currentPoint /= 60;
        if (currentPointMinutes.length() == 1)
        {
            currentPointMinutes = "0" + currentPointMinutes;
        }
        String currentPointHour = String.valueOf(currentPoint % 24);
        currentPoint /= 24;
        if (currentPointHour.length() == 1)
        {
            currentPointHour = "0" + currentPointHour;
        }

        Long trackLength = player.getPlayingTrack().getInfo().length;
        trackLength /= 1000;

        String trackLengthSeconds = String.valueOf(trackLength % 60);
        trackLength /= 60;
        if (trackLengthSeconds.length() == 1)
        {
            trackLengthSeconds = "0" + trackLengthSeconds;
        }
        String trackLengthMinutes = String.valueOf(trackLength % 60);
        trackLength /= 60;
        if (trackLengthMinutes.length() == 1)
        {
            trackLengthMinutes = "0" + trackLengthMinutes;
        }
        String trackLengthHour = String.valueOf(trackLength % 24);
        trackLength /= 24;
        if (trackLengthHour.length() == 1)
        {
            trackLengthHour = "0" + trackLengthHour;
        }

        return currentPointHour + "." + currentPointMinutes + "." + currentPointSeconds
                + " / " + trackLengthHour + "." + trackLengthMinutes + "." + trackLengthSeconds;
    }

    public ArrayList<TrackInfo> getQueueInfo()
    {
        return queue;
    }

    public Boolean getIsTrackLooped()
    {
        return isTrackLooped;
    }

    public Boolean getIsQueueLooped()
    {
        return isQueueLooped;
    }

    public void loadTrack(Integer id)
    {
        if ((id >= 0) && (id < queue.size()))
        {
            if (!endOfTrack)
            {
                fromLoadTrack = true;
                if (queue.size() == 0)
                {
                    guild.getAudioManager().closeAudioConnection();
                }
            }
            else if (queue.size() == 0)
            {
                fromLoadTrack = true;
                guild.getAudioManager().closeAudioConnection();
            }
            player.stopTrack();
            fromLoadTrack = false;

            // Если слева от текущего и затрагиваются загруженные
            if ((currentTrackNumber - id < downloadReserve) && (id < currentTrackNumber))
            {
                for (int i = 0; i < currentTrackNumber - id; i += 1)
                {
                    Response<File> loadTrackFromUrl = Video.loadTrackFromUrl(outputDir, queue.get(id + i).getUrl());
                    downloaded.add(0, loadTrackFromUrl);
                }
                for (int i = 0; i < currentTrackNumber - id; i += 1)
                {
                    if (downloaded.size() > downloadReserve)
                    {
                        downloaded.remove(downloaded.size() - 1);
                    }
                }
            }
            // Если справа от текущего и затрагиваются загруженные
            else if ((id - currentTrackNumber < downloadReserve) && (currentTrackNumber < id))
            {
                for (int i = 0; i < id - currentTrackNumber; i += 1)
                {
                    downloaded.get(0).data().delete();
                    downloaded.remove(0);
                }
                for (int i = id + downloaded.size(); i < Math.min(id + downloadReserve, queue.size()); i++)
                {
                    downloaded.add(Video.loadTrackFromUrl(outputDir, queue.get(i).getUrl()));
                }
            }
            else if (currentTrackNumber != id)
            {
                Integer downloadadSize = downloaded.size();
                for (int i = 0; i < downloadadSize; i += 1)
                {
                    downloaded.get(0).data().delete();
                    downloaded.remove(0);
                }
                for (int i = id; i < Math.min(id + downloadReserve, queue.size()); i++)
                {
                    downloaded.add(Video.loadTrackFromUrl(outputDir, queue.get(i).getUrl()));
                }
            }

            currentTrackNumber = id;
            if (downloaded.size() > 0)
            {
                PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
            }
            updateNowPlaying();
        }
    }

    public void updateNowPlaying()
    {
        TrackInfo nowPlayingInfo = getTrackInfo("Current");
        EmbedBuilder nowPlayingEmbed = new EmbedBuilder();

        nowPlayingEmbed.setColor(Color.decode("#2ECC71"));
        nowPlayingEmbed.setTitle("Сейчас играет:");

        nowPlayingEmbed.addField(nowPlayingInfo.getTitle(),
                nowPlayingInfo.getAuthor() + "\n" + nowPlayingInfo.getUrl(), false);

        textChannel.sendMessage(nowPlayingEmbed.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).submit();
    }
    
    public Integer getCurentTrackNumber()
    {
        return currentTrackNumber;
    }
}

