package dev.maiky.minetopia.modules.addons.addon;

import dev.maiky.minetopia.MinetopiaModule;
import lombok.Getter;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;

/**
 * Door: Maiky
 * Info: Minetopia - 13 Jun 2021
 * Package: dev.maiky.minetopia.modules.addons.addon
 */

public abstract class Addon implements MinetopiaModule {

	@Getter
	private final CompositeTerminable terminable = CompositeTerminable.create();
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
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			getTerminable().close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "???";
	}

}
