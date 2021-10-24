package com.djusufcompany.discordmusicbot.commands;


import java.util.ArrayList;
import java.util.Arrays;


public abstract class CommandsGenerator
{
    public static ArrayList<Command> getCommands()
    {
        Command[] commandsArray =
        {
            Play.getInstance(),
            Skip.getInstance(),
            Info.getInstance(),
            Queue.getInstance(),
            Loop.getInstance(),
            Loopq.getInstance(),
            Remove.getInstance(),
            Clear.getInstance(),
            Exit.getInstance(),
            Help.getInstance()
        };
        return new ArrayList<>(Arrays.asList(commandsArray));
    }
}

