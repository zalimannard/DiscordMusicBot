package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Exit extends Command
{
    private static Exit INSTANCE;

    private Exit()
    {
        commandName = "exit";
        description = "Отключение бота от канала без очищения очереди";
    }

    public static Exit getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Exit();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        event.getMember().getGuild().getAudioManager().closeAudioConnection();
        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.pause();
        
        EmbedBuilder queueEmbed = new EmbedBuilder();
        queueEmbed.setColor(Color.decode("#2ECC71"));
        queueEmbed.setTitle("Пока-пока");
        event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
    }
}

