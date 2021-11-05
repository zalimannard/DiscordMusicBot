package com.djusufcompany.discordmusicbot;


import com.djusufcompany.discordmusicbot.commands.Info;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
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
    private final Integer downloadReserve;
    private final AudioPlayer player;
    private final Guild guild;
    private File outputDir;
    private ArrayList<TrackInfo> queue;
    private ArrayList<Response<File>> downloaded;
    private Integer idCurrentTrack;
    private Boolean isTrackLooped;
    private Boolean isQueueLooped;

    private Boolean endOfTrack;
    private Boolean fromJump;
    private Boolean nothingPlaying;

    public TrackScheduler(AudioPlayer player, Guild guild)
    {
        this.downloadReserve = 3;
        this.player = player;
        this.guild = guild;
        this.queue = new ArrayList<>();
        this.downloaded = new ArrayList<>();
        this.idCurrentTrack = 0;
        this.isTrackLooped = false;
        this.isQueueLooped = false;

        try
        {
            this.outputDir = Files.createTempDirectory("Video").toFile();
        }
        catch (IOException ex)
        {
            Logger.getLogger(TrackScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.endOfTrack = false;
        this.fromJump = false;
        this.nothingPlaying = true;
    }

    public void jumpTo(Integer number)
    {
        Info.deleteNowPlaying();
        Integer idNewTrack = number - 1;
        if ((idNewTrack >= 0) && (idNewTrack < queue.size()))
        {
            // Если трек закончился не сам
            if ((!endOfTrack) && (queue.size() > 1))
            {
                fromJump = true;
                if (queue.size() == 0)
                {
                    leaveFromChannel();
                    nothingPlaying = true;
                }
            }
            // Для случая удаления
            else if (queue.size() == 0)
            {
                fromJump = true;
                leaveFromChannel();
                nothingPlaying = true;
            }
            player.stopTrack();
            fromJump = false;

            // Загрузка трека без играющих предыдущих
            if (downloaded.size() == 0)
            {
                downloaded.add(Video.loadTrackFromVideoId(outputDir, queue.get(idNewTrack).getVideoId()));
                PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
                nothingPlaying = false;
            }
            // Загрузка текущего трека
            else if (idCurrentTrack == idNewTrack)
            {
                PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
                nothingPlaying = false;
            }
            // Загрузка трека слева от текущего с возможностью сохранения предзагруженных
            else if ((idNewTrack < idCurrentTrack) && (idCurrentTrack - idNewTrack < downloadReserve))
            {
                for (int i = 0; i < idCurrentTrack - idNewTrack; i += 1)
                {
                    if (downloaded.size() > downloadReserve)
                    {
                        downloaded.get(downloaded.size() - 1).data().delete();
                        downloaded.remove(downloaded.size() - 1);
                    }
                }
                // Добавление нового проигрываемого трека
                Response<File> loadPlayingTrackFromUrl = Video.loadTrackFromVideoId(outputDir, queue.get(idNewTrack).getVideoId());
                downloaded.add(0, loadPlayingTrackFromUrl);
                // Начинаем его играть, чтобы пользователь не ждал
                PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
                nothingPlaying = false;
                // Загружаем остальные треки
                for (int i = 1; i < idCurrentTrack - idNewTrack; i += 1)
                {
                    Response<File> loadTrackFromUrl = Video.loadTrackFromVideoId(outputDir, queue.get(idNewTrack + i).getVideoId());
                    downloaded.add(0, loadTrackFromUrl);
                }
            }
            // Загрузка трека справа от текущего с возможностью сохранения предзагруженных
            else if ((idNewTrack > idCurrentTrack) && (idNewTrack - idCurrentTrack < downloadReserve) && (downloaded.size() > 0))
            {
                PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(idNewTrack - idCurrentTrack).data().getAbsolutePath());
                nothingPlaying = false;
                for (int i = 0; i < idNewTrack - idCurrentTrack; i += 1)
                {
                    downloaded.get(0).data().delete();
                    downloaded.remove(0);
                }
                for (int i = idNewTrack + downloaded.size(); i < Math.min(idNewTrack + downloadReserve, queue.size()); i++)
                {
                    downloaded.add(Video.loadTrackFromVideoId(outputDir, queue.get(i).getVideoId()));
                }
            }
            // Загрузка трека, не затрагивающего предзагруженные
            else
            {
                while (downloaded.size() > 0)
                {
                    downloaded.get(0).data().delete();
                    downloaded.remove(0);
                }
                downloaded.add(Video.loadTrackFromVideoId(outputDir, queue.get(idNewTrack).getVideoId()));
                PlayerManager.getInstance().loadAndPlay(guild, downloaded.get(0).data().getAbsolutePath());
                nothingPlaying = false;
                for (int i = idNewTrack + 1; i < Math.min(idNewTrack + downloadReserve, queue.size()); i++)
                {
                    downloaded.add(Video.loadTrackFromVideoId(outputDir, queue.get(i).getVideoId()));
                }
            }
            idCurrentTrack = idNewTrack;
            Info.getInstance().printNowPlaying(queue.get(idCurrentTrack));
            resume();
        }
    }

    public void insertTrack(Integer number, String videoId, Boolean withNotification)
    {
        Integer id = number;
        if ((id >= 0) && (id <= queue.size()))
        {
            YoutubeDownloader downloader = null;
            VideoInfo video = null;
            try
            {
                downloader = new YoutubeDownloader();
                video = Video.getVideoInfo(downloader, videoId);
            }
            catch (Exception e)
            {
            }
            if ((id - idCurrentTrack < downloadReserve) && (idCurrentTrack < id))
            {
                while (downloaded.size() > 1)
                {
                    downloaded.get(1).data().delete();
                    downloaded.remove(1);
                }
            }

            queue.add(id, new TrackInfo(
                    videoId,
                    video.details().title(),
                    video.details().author(),
                    new Long(video.details().lengthSeconds()) * 1000)
            );

            if (withNotification)
            {
                Info.getInstance().printTrackAdded(getTrackInfo(id + 1));
            }

            if ((id <= idCurrentTrack) && (!nothingPlaying))
            {
                idCurrentTrack += 1;
            }
            if ((id - idCurrentTrack < downloadReserve) && (idCurrentTrack < id))
            {
                for (int i = idCurrentTrack + 1; i < Math.min(queue.size(), idCurrentTrack + downloadReserve); i += 1)
                {
                    downloaded.add(Video.loadTrackFromVideoId(outputDir, queue.get(i).getVideoId()));
                }
            }
            if (nothingPlaying)
            {
                jumpTo(number + 1);
            }
        }
    }

    public void remove(Integer number)
    {
        Integer idDeletingTrack = number - 1;
        if ((idDeletingTrack >= 0) && (idDeletingTrack < queue.size()))
        {
            queue.remove(queue.get(idDeletingTrack));

            if (idDeletingTrack == idCurrentTrack)
            {
                downloaded.get(0).data().delete();
                downloaded.remove(0);
                if (idCurrentTrack + downloadReserve - 1 < queue.size())
                {
                    downloaded.add(Video.loadTrackFromVideoId(outputDir, queue.get(idCurrentTrack + downloadReserve - 1).getVideoId()));
                }
                if (downloaded.size() > 0)
                {
                    jumpTo(idCurrentTrack + 1);
                }
                else if ((isQueueLooped) && (queue.size() > 0))
                {
                    jumpTo(1);
                }
                else
                {
                    endOfTrack = true;
                    player.stopTrack();
                    leaveFromChannel();
                    nothingPlaying = true;
                }
            }
            else if (idDeletingTrack < idCurrentTrack)
            {
                idCurrentTrack -= 1;
            }
            else if (idDeletingTrack - idCurrentTrack < downloadReserve)
            {
                downloaded.get(idDeletingTrack - idCurrentTrack).data().delete();
                downloaded.remove(idDeletingTrack - idCurrentTrack);
                if (idCurrentTrack + downloadReserve - 1 < queue.size())
                {
                    downloaded.add(Video.loadTrackFromVideoId(outputDir, queue.get(idCurrentTrack + downloadReserve - 1).getVideoId()));
                }
            }
        }
    }

    public void play(AudioTrack track)
    {
        player.startTrack(track, true);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if (fromJump)
        {
            fromJump = false;
        }
        else
        {
            endOfTrack = true;
            if (isTrackLooped)
            {
                jumpTo(idCurrentTrack + 1);
            }
            else if ((isQueueLooped) && (idCurrentTrack + 1 == queue.size()))
            {
                jumpTo(1);
            }
            else if (idCurrentTrack + 1 == queue.size())
            {
                leaveFromChannel();
                nothingPlaying = true;
            }
            else
            {
                jumpTo(idCurrentTrack + 2);
            }
            endOfTrack = false;
        }
    }

    public void setTrackTime(String time)
    {
        Long newTimeInLong = Time.timeStringToValue(time);
        Long trackLength = player.getPlayingTrack().getInfo().length;

        if ((newTimeInLong >= 0) && (newTimeInLong < trackLength))
        {
            player.getPlayingTrack().setPosition(newTimeInLong);
        }
    }

    public String getCurrentTrackTimeHms()
    {
        Long currentPoint = player.getPlayingTrack().getPosition();
        Long trackLength = player.getPlayingTrack().getInfo().length;

        return Time.timeValueToStringHms(currentPoint) + " / " + Time.timeValueToStringHms(trackLength);
    }

    public TrackInfo getTrackInfo(Integer number)
    {
        return queue.get(number - 1);
    }

    public Integer getQueueSize()
    {
        return queue.size();
    }

    public Integer getCurrentTrackNumber()
    {
        return idCurrentTrack + 1;
    }

    public Integer getDownloadReserve()
    {
        return downloadReserve;
    }

    public void pause()
    {
        player.setPaused(true);
    }

    public void resume()
    {
        player.setPaused(false);
    }

    public Boolean isTrackLooped()
    {
        return isTrackLooped;
    }

    public void changeTrackLoopMode()
    {
        isTrackLooped = !isTrackLooped;
    }

    public Boolean isQueueLooped()
    {
        return isQueueLooped;
    }

    public void changeQueueLoopMode()
    {
        isQueueLooped = !isQueueLooped;
    }

    public void endTrack()
    {
        player.stopTrack();
    }

    private void leaveFromChannel()
    {
        guild.getAudioManager().closeAudioConnection();
        if (isTrackLooped())
        {
            changeTrackLoopMode();
        }
        if (isQueueLooped())
        {
            changeQueueLoopMode();
        }
        Info.deleteNowPlaying();
    }
}

