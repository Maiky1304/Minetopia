package dev.maiky.minetopia.modules.boosters;

import co.aikar.commands.BukkitCommandManager;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.boosters.commands.BoosterCommand;
import dev.maiky.minetopia.modules.boosters.enums.BoosterType;
import dev.maiky.minetopia.modules.boosters.tasks.BoostTask;
import me.lucko.helper.terminable.composite.CompositeTerminable;

/**
 * Door: Maiky
 * Info: Minetopia - 06 Jun 2021
 * Package: dev.maiky.minetopia.modules.boosters
 */

public class BoosterModule implements MinetopiaModule {

	private CompositeTerminable composite = CompositeTerminable.create();
	private boolean enabled = false;

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

		// Commands
		this.registerCommands();

		// Tasks
		this.registerTasks();
	}

	private void registerTasks() {
		BoostTask boostTask = new BoostTask();
		boostTask.setup(this.composite);
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager manager = minetopia.getCommandManager();

		manager.getCommandCompletions().registerStaticCompletion("boosterTypes", BoosterType.list());
		manager.registerCommand(new BoosterCommand());
	}

	@Override
	public void disable() {
		this.enabled = false;
	}

	@Override
	public String getName() {
		return "Boosters";
	}
}
