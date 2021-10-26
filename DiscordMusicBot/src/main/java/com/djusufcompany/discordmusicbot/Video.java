package com.djusufcompany.discordmusicbot;


import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import java.io.File;


public abstract class Video
{
    public static Response<File> loadTrackFromUrl(File outputDir, String url)
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
            RequestVideoFileDownload requestFile = new RequestVideoFileDownload(format)
                    .saveTo(outputDir)
                    .renameTo("video");
            return downloader.downloadVideoFile(requestFile);
        }
        return null;
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

    public static VideoInfo getVideoInfo(YoutubeDownloader downloader, String url)
    {
        String videoId = urlToId(url);
        RequestVideoInfo requestInfo = new RequestVideoInfo(videoId);
        Response<VideoInfo> responseInfo = downloader.getVideoInfo(requestInfo);
        return responseInfo.data();
    }
}

