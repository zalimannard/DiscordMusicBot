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
            Skipto.getInstance(),
            Prev.getInstance(),
            Insert.getInstance(),
            Info.getInstance(),
            Queue.getInstance(),
            Loop.getInstance(),
            Loopq.getInstance(),
            Remove.getInstance(),
            Clear.getInstance(),
            Pause.getInstance(),
            Resume.getInstance(),
            Join.getInstance(),
            Shuffle.getInstance(),
            Exit.getInstance(),
            Lasttrack.getInstance(),
            Help.getInstance()
        };
        return new ArrayList<>(Arrays.asList(commandsArray));
    }
}

