package com.djusufcompany.discordmusicbot.commands;


import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Help extends Command
{
    private static Help INSTANCE;

    private Help()
    {
        commandName = "help";
        description = "Вызов этого меню";
    }

    public static Help getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Help();
        }
        return INSTANCE;
    }

    public void execute(MessageReceivedEvent event)
    {
        EmbedBuilder help = new EmbedBuilder();
        help.setTitle("Команды USUF BOT:");
        help.setColor(Color.decode("#2ECC71"));

        ArrayList<Command> commands = CommandsGenerator.getCommands();

        for (Command command : commands)
        {
            help.addField("-" + command.getCommandName() + " " + command.getArguments(),
                    command.getDescription(), false);
        }

        event.getChannel().sendMessage(help.build()).delay(180, TimeUnit.SECONDS).flatMap(Message::delete).submit();
    }
}

