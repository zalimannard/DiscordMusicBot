package com.djusufcompany.discordmusicbot;


import com.djusufcompany.discordmusicbot.commands.Exit;
import com.djusufcompany.discordmusicbot.commands.Help;
import com.djusufcompany.discordmusicbot.commands.Next;
import com.djusufcompany.discordmusicbot.commands.Play;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class MessageHandler extends ListenerAdapter
{
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String message = event.getMessage().getContentRaw();
        if (message.charAt(0) != '-')
        {
            return;
        }

        MessageChannel channel = event.getMessage().getChannel();
        String command = (message.substring(1)).split(" ")[0];

        if (command.equalsIgnoreCase("play"))
        {
            Play.execute(event);
        }
        else if (command.equalsIgnoreCase("help"))
        {
            Help.execute(event);
        }
        else if (command.equalsIgnoreCase("exit"))
        {
            Exit.execute(event);
        }
        else if (command.equalsIgnoreCase("skip"))
        {
            Next.execute(event);
        }
    }

}

