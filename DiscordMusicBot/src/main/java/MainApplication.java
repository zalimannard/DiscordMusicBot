

import com.djusufcompany.discordmusicbot.MessageHandler;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;


public class MainApplication
{
    public static void main(String[] args) throws LoginException
    {
        JDA jda = JDABuilder.createDefault("ODk5NzE1NDkzMDE4MDk5Nzcy.YW2zWQ.1z90off5bxPE8RLdDUol90-39UE")
                .addEventListeners(new MessageHandler())
                .setActivity(Activity.playing("School Simulator"))
                .build();

        while (true)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
        }
    }
}

