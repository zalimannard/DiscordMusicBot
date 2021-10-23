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

