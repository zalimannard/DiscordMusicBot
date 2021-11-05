package com.djusufcompany.discordmusicbot;


public abstract class Time
{
    public static String timeValueToStringHms(Long time)
    {
        time /= 1000;

        String seconds = String.valueOf(time % 60);
        time /= 60;
        if (seconds.length() == 1)
        {
            seconds = "0" + seconds;
        }
        String minutes = String.valueOf(time % 60);
        time /= 60;
        if (minutes.length() == 1)
        {
            minutes = "0" + minutes;
        }
        String hours = String.valueOf(time % 24);
        time /= 24;
        if (hours.length() == 1)
        {
            hours = "0" + hours;
        }
        return hours + ":" + minutes + ":" + seconds;
    }

    public static Long timeStringToValue(String timeString)
    {
        String[] hms = timeString.split(":");
        Long timeLong = 0L;

        if (hms.length == 1)
        {
            timeLong += Long.valueOf(hms[0]) * 1000;
        }
        else if (hms.length == 2)
        {
            timeLong += 0
                    + Long.valueOf(hms[0]) * 60 * 1000
                    + Long.valueOf(hms[1]) * 1000;
        }
        else if (hms.length == 3)
        {
            timeLong += 0
                    + Long.valueOf(hms[0]) * 60 * 60 * 1000
                    + Long.valueOf(hms[1]) * 60 * 1000
                    + Long.valueOf(hms[2]) * 1000;
        }

        return timeLong;
    }
}

