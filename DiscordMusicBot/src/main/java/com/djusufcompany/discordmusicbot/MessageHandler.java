package com.djusufcompany.discordmusicbot;


import com.djusufcompany.discordmusicbot.commands.Command;
import com.djusufcompany.discordmusicbot.commands.CommandsGenerator;
import java.util.ArrayList;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class MessageHandler extends ListenerAdapter
{
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String message = null;
        try
        {
            message = event.getMessage().getContentRaw();
            if (message.charAt(0) != '-')
            {
                return;
            }

            String inputCommand = (message.substring(1)).split(" ")[0];

            ArrayList<Command> commands = CommandsGenerator.getCommands();
            for (Command command : commands)
            {
                if (inputCommand.equals(command.getCommandName()))
                {
                    command.execute(event);
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
    {
        if (event.getMember().getGuild().getMembers().size() == 1)
        {
            event.getMember().getGuild().getAudioManager().closeAudioConnection();
            PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.pause();
        }
    }

}

