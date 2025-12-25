package com.profiluefter.vibebotv3;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VibeCommandListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(VibeCommandListener.class);

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!"vibe".equals(event.getName())) {
            return;
        }

        var option = event.getOption("prompt");
        String prompt = option == null ? null : option.getAsString();

        if (prompt == null || prompt.isBlank()) {
            event.reply("Please provide a prompt, e.g. /vibe <what you want changed>.").setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue(hook -> {
            try {
                GithubDispatcher dispatcher = GithubDispatcher.fromEnv();
                GithubDispatcher.DispatchResult result = dispatcher.dispatchJunieWorkflow(prompt);

                if (result.success()) {
                    hook.sendMessage("Workflow dispatched successfully. I'll keep vibing while Junie works on it!\n" +
                            "Prompt: " + prompt).queue();
                } else {
                    hook.sendMessage("Failed to dispatch workflow (HTTP " + result.statusCode() + "). " +
                            "Please try again later.").queue();
                }
            } catch (IllegalStateException e) {
                logger.warn("Vibe command configuration error: {}", e.getMessage());
                hook.sendMessage("Configuration error: " + e.getMessage()).queue();
            } catch (Exception e) {
                logger.error("Error dispatching Junie workflow", e);
                hook.sendMessage("An unexpected error occurred while dispatching the workflow.").queue();
            }
        });
    }
}
