package dev.maiky.minetopia.modules.bags;

import co.aikar.commands.BukkitCommandManager;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.bags.bag.BagType;
import dev.maiky.minetopia.modules.bags.commands.BagCommand;
import dev.maiky.minetopia.modules.bags.listeners.BagOpenListener;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;

/**
 * Door: Maiky
 * Info: Minetopia - 26 May 2021
 * Package: dev.maiky.minetopia.modules.bags
 */

public class BagsModule implements MinetopiaModule {

	private boolean enabled;

	private final CompositeTerminable composite = CompositeTerminable.create();

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

		// Events
		this.registerEvents();
	}

	private void registerEvents() {
		this.composite.bindModule(new BagOpenListener());
	}

	private void registerCommands() {
		BukkitCommandManager commandManager = Minetopia.getPlugin(Minetopia.class).getCommandManager();

		commandManager.getCommandCompletions().registerStaticCompletion("bagTypes", BagType.list());
		commandManager.registerCommand(new BagCommand());
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
		return "Bags";
	}
}
