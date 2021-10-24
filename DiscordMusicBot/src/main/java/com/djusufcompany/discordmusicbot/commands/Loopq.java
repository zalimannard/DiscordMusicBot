/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Loopq extends CommandData implements Command
{
    private static Loopq INSTANCE;

    private Loopq()
    {
        commandName = "loopq";
        description = "Вкл./Выкл. зацикливание очереди";
    }

    public static Loopq getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Loopq();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.changeQueueLoopMode();
    }
}

