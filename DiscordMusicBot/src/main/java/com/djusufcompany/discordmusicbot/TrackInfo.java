package com.djusufcompany.discordmusicbot;


public class TrackInfo
{
    private String author;
    private String title;
    private String url;

    public TrackInfo(String author, String title, String url)
    {
        this.author = author;
        this.title = title;
        this.url = url;
    }

    public TrackInfo(TrackInfo info)
    {
        this.author = info.getAuthor();
        this.title = info.getTitle();
        this.url = info.getUrl();
    }

    public String getAuthor()
    {
        return author;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }
}

