package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Resume extends Command
{
    private static Resume INSTANCE;

    private Resume()
    {
        commandName = "resume";
        description = "Продолжить проигрывание трека";
    }

    public static Resume getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Resume();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        if (event.getMember().getVoiceState().inVoiceChannel())
        {
            PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.resume();

            EmbedBuilder queueEmbed = new EmbedBuilder();
            queueEmbed.setColor(Color.decode("#2ECC71"));
            queueEmbed.setTitle("Воспроизведение продолжено");
            event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
        }
    }
}

