package dev.maiky.minetopia.modules.prefixes;

import co.aikar.commands.BukkitCommandManager;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.prefixes.commands.PrefixCommand;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.prefixes
 */

public class PrefixesModule implements MinetopiaModule {

	private boolean enabled;

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
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager commandManager = minetopia.getCommandManager();

		commandManager.registerCommand(new PrefixCommand());
	}

	@Override
	public void disable() {
		this.enabled = false;
	}

	@Override
	public String getName() {
		return "Prefixes";
	}
}
