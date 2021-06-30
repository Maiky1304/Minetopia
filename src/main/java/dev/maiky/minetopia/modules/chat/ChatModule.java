package dev.maiky.minetopia.modules.chat;

import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.chat.listeners.MainChatListener;
import dev.maiky.minetopia.modules.chat.listeners.RadioListener;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;

/**
 * Door: Maiky
 * Info: Minetopia - 22 May 2021
 * Package: dev.maiky.minetopia.modules.chat
 */

public class ChatModule implements MinetopiaModule {

	private final CompositeTerminable composite = CompositeTerminable.create();

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

		// Register Events
		this.registerEvents();
	}

	private void registerEvents() {
		this.composite.bindModule(new MainChatListener());
		this.composite.bindModule(new RadioListener());
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
		return "Chat";
	}
}
