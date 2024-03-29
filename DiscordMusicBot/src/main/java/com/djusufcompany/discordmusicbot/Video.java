package com.djusufcompany.discordmusicbot;


import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestPlaylistInfo;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.playlist.PlaylistInfo;
import com.github.kiulian.downloader.model.playlist.PlaylistVideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public abstract class Video
{

    public static Response<File> loadTrackFromVideoId(File outputDir, String videoId)
    {
        YoutubeDownloader downloader = new YoutubeDownloader();
        VideoInfo video = getVideoInfo(downloader, videoId);

        Format format = video.bestAudioFormat();
        if (format == null)
        {
            format = video.bestVideoWithAudioFormat();
        }
        if (format != null)
        {
            RequestVideoFileDownload requestFile = new RequestVideoFileDownload(format)
                    .saveTo(outputDir)
                    .renameTo("video");
            return downloader.downloadVideoFile(requestFile);
        }
        return null;
    }

    public static ArrayList<String> getTracksVideoIdFromPlaylist(String url)
    {
        ArrayList<String> answer = new ArrayList<String>();
        String playlistId = null;
        String[] urlTypeArray =
        {
            "https://www.youtube.com/playlist?list=",
            "https://youtube.com/playlist?list="
        };
        for (String urlType : urlTypeArray)
        {
            if (url.contains(urlType))
            {
                playlistId = url.substring(urlType.length());
            }
        }
        YoutubeDownloader downloader = new YoutubeDownloader();
        RequestPlaylistInfo request = new RequestPlaylistInfo(playlistId);
        Response<PlaylistInfo> response = downloader.getPlaylistInfo(request);
        PlaylistInfo playlistInfo = response.data();
        List<PlaylistVideoDetails> tracks = playlistInfo.videos();
        for (PlaylistVideoDetails track : tracks)
        {
            answer.add(track.videoId());
        }
        return answer;
    }

    public static String urlToId(String url)
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

    public static VideoInfo getVideoInfo(YoutubeDownloader downloader, String videoId)
    {
        RequestVideoInfo requestInfo = new RequestVideoInfo(videoId);
        Response<VideoInfo> responseInfo = downloader.getVideoInfo(requestInfo);
        return responseInfo.data();
    }
}

