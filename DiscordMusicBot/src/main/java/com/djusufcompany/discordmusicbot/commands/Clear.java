/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Clear extends CommandData implements Command
{
    private static Clear INSTANCE;

    private Clear()
    {
        commandName = "clear";
        description = "Очистить текущую очередь";
    }

    public static Clear getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Clear();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        Integer size = PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.getQueueInfo().size();
        for (int i = 0; i < size; i++)
        {
            PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.remove(0);
        }
    }
}

