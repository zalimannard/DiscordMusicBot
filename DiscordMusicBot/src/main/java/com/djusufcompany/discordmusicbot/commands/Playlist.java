/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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



public class Playlist extends Command
{
    private static Playlist INSTANCE;

    private Playlist()
    {
        commandName = "playlist";
        arguments = "(url)";
        description = "Добавление плейлиста в очередь";
    }

    public static Playlist getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Playlist();
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
                    .addPlaylistToQueue(
                            trackUrl,
                            event.getMessage().getTextChannel());
        }

        PlayerManager.getInstance().getMusicManager(event.getMember().getGuild()).scheduler.resume();

        EmbedBuilder queueEmbed = new EmbedBuilder();
        queueEmbed.setColor(Color.decode("#2ECC71"));
        queueEmbed.setTitle("Плейлист добавлен в очередь");
        event.getChannel().sendMessage(queueEmbed.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
    }
}

