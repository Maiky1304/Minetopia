package dev.maiky.minetopia.modules.leaderboards;

import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.leaderboards.tasks.DatabaseTask;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.leaderboards
 */

public class LeaderboardsModule implements MinetopiaModule {

	private boolean enabled;
	private final CompositeTerminable terminable = CompositeTerminable.create();

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

		// Register Tasks
		Schedulers.sync().runRepeating(new DatabaseTask(DataModule.getInstance().getSqlHelper()), 0, 20 * 1800).bindWith(terminable);

		// Register Commands
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			this.terminable.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Leaderboards";
	}
}
