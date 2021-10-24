package com.djusufcompany.discordmusicbot;


import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;


public class Main
{
    public static void main(String[] args) throws LoginException
    {
        JDA jda = JDABuilder.createDefault(ConfProperties.getProperty("token"))
                .addEventListeners(new MessageHandler())
                .setActivity(Activity.playing("School Simulator"))
                .build();
    }
}

