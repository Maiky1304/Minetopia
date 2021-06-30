package dev.maiky.minetopia.modules.levels;

import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.levels.listeners.LevelCheckListener;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.levels
 */

public class LevelsModule implements MinetopiaModule {

	private final CompositeTerminable composite = CompositeTerminable.create();
	private boolean enabled;

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.enable();
		this.disable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		// Events
		this.registerEvents();
	}

	private void registerEvents() {
		this.composite.bindModule(new LevelCheckListener());
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
		return "Levels";
	}
}
