package dev.maiky.minetopia.modules.discord;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.discord.commands.StatsCommand;
import dev.maiky.minetopia.modules.discord.listeners.MinecraftCommandLogger;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;

/**
 * Door: Maiky
 * Info: Minetopia - 06 Jun 2021
 * Package: dev.maiky.minetopia.modules.discord
 */

public class DiscordModule implements MinetopiaModule {

	private final CompositeTerminable composite = CompositeTerminable.create();
	private boolean enabled;

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void reload() {
		this.disable();
		this.enable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		// Main Class
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);

		// Bot Token
		final String botToken = minetopia.getConfiguration().get().getString("discord.token");
		if (botToken.length() == 0) {
			Bukkit.getLogger().info("Discord Module is being disabled due to it being disabled for this server in the configuration.");
			this.disable();
			return;
		}

		// Initialize JDA
		JDABuilder builder = JDABuilder.createDefault(botToken);
		JDA jda;
		try {
			jda = builder.build();
		} catch (LoginException e) {
			Bukkit.getLogger().warning("Failed to hook into Discord might be an invalid token check error below.");
			e.printStackTrace();
			this.disable();
			return;
		}

		// JDA Ready
		try {
			jda.awaitReady();
		} catch (InterruptedException e) {
			Bukkit.getLogger().warning("Failed to hook into Discord might be an invalid token check error below.");
			e.printStackTrace();
			this.disable();
			return;
		}

		// Commands
		Guild guild = jda.getGuildById(654593439303991306L);
		if (guild == null) return;

		guild.updateCommands().addCommands(
				new CommandData("stats", "Bekijk de Minetopia statistieken van een speler.")
						.addOptions(new OptionData(OptionType.STRING, "ign", "In-Game naam van de minecraft speler")
								.setRequired(true))
		).queue();
		jda.addEventListener(new StatsCommand());

		MinecraftCommandLogger logger = new MinecraftCommandLogger(jda);
		logger.register();
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			this.composite.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Discord Hook";
	}
}
