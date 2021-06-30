package dev.maiky.minetopia.modules.ddgitems;

import co.aikar.commands.BukkitCommandManager;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.ddgitems.commands.DDGItemsCommand;
import dev.maiky.minetopia.modules.ddgitems.items.ItemLoader;
import dev.maiky.minetopia.modules.ddgitems.items.classes.ItemType;

/**
 * Door: Maiky
 * Info: Minetopia - 26 May 2021
 * Package: dev.maiky.minetopia.modules.ddgitems
 */

public class DDGItemsModule implements MinetopiaModule {

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

		// Item Loader
		this.initializeItems();

		// Commands
		this.registerCommands();
	}

	private void initializeItems() {
		ItemLoader loader = new ItemLoader();
		try {
			loader.load();
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager commandManager = minetopia.getCommandManager();

		commandManager.getCommandCompletions().registerStaticCompletion("itemTypes", ItemType.list());
		commandManager.registerCommand(new DDGItemsCommand());
	}

	@Override
	public void disable() {
		this.enabled = false;
	}

	@Override
	public String getName() {
		return "DDGItems";
	}
}
