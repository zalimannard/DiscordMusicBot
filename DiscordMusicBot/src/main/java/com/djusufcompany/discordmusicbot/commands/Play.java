package com.djusufcompany.discordmusicbot.commands;


import com.djusufcompany.discordmusicbot.PlayerManager;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;


public class Play extends Command
{
    private static Play INSTANCE;

    private Play()
    {
        commandName = "play";
        arguments = "(url)";
        description = "Добавление трека в очередь";
    }

    public static Play getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Play();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        Member member = event.getMember();
        String trackUrl = event.getMessage().getContentRaw().split(" ")[1];

        if (member.getVoiceState().inVoiceChannel())
        {
            final AudioManager audioManager = member.getGuild().getAudioManager();
            final VoiceChannel memberChannel = member.getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
            PlayerManager.getInstance().getMusicManager(member.getGuild()).scheduler
                    .addTrackToQueue(
                            trackUrl,
                            event.getMessage().getTextChannel());
        }

        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.resume();

        EmbedBuilder queueEmbed = new EmbedBuilder();
        queueEmbed.setColor(Color.decode("#2ECC71"));
        String trackName = PlayerManager.getInstance().getMusicManager(member.getGuild()).scheduler.getTrackInfo("New").getTitle();
        queueEmbed.setTitle("Трек \"" + trackName +"\" добавлен в очередь");
        event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
    }
}

