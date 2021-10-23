package com.djusufcompany.discordmusicbot.commands;


import java.awt.Color;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Help extends CommandData implements Command
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
            help.addField(command.getCommandName() + " " + command.getArguments(),
                command.getDescription(), false);
        }

        event.getChannel().sendMessage(help.build()).queue();
    }
}

