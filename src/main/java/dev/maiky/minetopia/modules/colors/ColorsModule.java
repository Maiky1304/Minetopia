package dev.maiky.minetopia.modules.colors;

import co.aikar.commands.BukkitCommandManager;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.colors.commands.ChatColorCommand;
import dev.maiky.minetopia.modules.colors.commands.LevelColorCommand;
import dev.maiky.minetopia.modules.colors.commands.PrefixColorCommand;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.colors
 */

public class ColorsModule implements MinetopiaModule {

	private boolean enabled = false;

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.disable();
		this.enable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		// Commands
		this.registerCommands();
	}

	private void registerCommands() {
		BukkitCommandManager commandManager = Minetopia.getPlugin(Minetopia.class).getCommandManager();

		commandManager.registerCommand(new ChatColorCommand());
		commandManager.registerCommand(new LevelColorCommand());
		commandManager.registerCommand(new PrefixColorCommand());
	}

	@Override
	public void disable() {
		this.enabled = false;
	}

	@Override
	public String getName() {
		return "Colors";
	}
}
