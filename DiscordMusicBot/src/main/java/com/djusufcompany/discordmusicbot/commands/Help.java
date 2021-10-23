package com.djusufcompany.discordmusicbot.commands;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public abstract class Help
{
    public static void execute(MessageReceivedEvent event)
    {
        event.getChannel().sendMessage(""
                + "USUF BOT:\n"
                + "-play - добавление трека в очередь\n"
                + "-skip - пропуск текущего трека\n"
                + "-exit - удаление бота из голосового канала\n"
                + "-help - это меню\n")
                .submit();
    }
}

