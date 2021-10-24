/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;



public class Prev extends Command
{
    private static Prev INSTANCE;

    private Prev()
    {
        commandName = "prev";
        description = "Воспроизвести предыдущий трек";
    }

    public static Prev getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Prev();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.previousTrack();
    }
}

