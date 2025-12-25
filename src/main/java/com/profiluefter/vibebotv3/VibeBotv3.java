package com.profiluefter.vibebotv3;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VibeBotv3 {
    private static final Logger logger = LoggerFactory.getLogger(VibeBotv3.class);
    
    public static void main(String[] args) {
        logger.info("Starting VibeBotv3...");
        
        // Get bot token from environment variable
        String botToken = System.getenv("BOT_TOKEN");
        if (botToken == null || botToken.isEmpty()) {
            logger.error("BOT_TOKEN environment variable is not set!");
            System.exit(1);
        }
        
        try {
            // Build and configure the JDA instance
            JDA jda = JDABuilder.createDefault(botToken)
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.MESSAGE_CONTENT
                    )
                    .setActivity(Activity.playing("VibeBotv3"))
                    .build();
            
            // Wait for JDA to be ready
            jda.awaitReady();
            
            // Register listeners
            jda.addEventListener(new VibeCommandListener());

            // Register slash commands
            jda.updateCommands().addCommands(
                    Commands.slash("vibe", "Trigger Junie workflow to propose changes to the bot functionality.")
                            .addOption(OptionType.STRING, "prompt", "Describe the change you want. This may update the bot's functionality.", true)
            ).queue();

            logger.info("VibeBotv3 is now online and ready!");
            logger.info("Connected to {} guilds", jda.getGuilds().size());
            
        } catch (Exception e) {
            logger.error("Failed to start VibeBotv3", e);
            System.exit(1);
        }
    }
}
