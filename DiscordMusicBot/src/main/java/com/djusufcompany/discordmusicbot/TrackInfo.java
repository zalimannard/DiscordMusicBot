package com.djusufcompany.discordmusicbot;


public class TrackInfo
{
    private final String videoId;
    private final String title;
    private final String author;
    private final Long duration;

    public TrackInfo(String videoId, String title, String author, Long duration)
    {
        this.videoId = videoId;
        this.title = title;
        this.author = author;
        this.duration = duration;
    }

    public String getVideoId()
    {
        return videoId;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }

    public Long getDuration()
    {
        return duration;
    }
}

