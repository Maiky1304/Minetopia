package dev.maiky.minetopia.modules.transportation;

import co.aikar.commands.BukkitCommandManager;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.managers.PortalManager;
import dev.maiky.minetopia.modules.transportation.commands.TransportationCommand;
import dev.maiky.minetopia.modules.transportation.listeners.SignChangeListener;
import dev.maiky.minetopia.modules.transportation.listeners.TeleporterUseListener;
import dev.maiky.minetopia.util.Configuration;
import lombok.Getter;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.transportation
 */

public class TransportationModule implements MinetopiaModule {

	@Getter
	private final PortalManager portalManager;

	private final CompositeTerminable composite = CompositeTerminable.create();
	private boolean enabled = false;

	@Getter
	private final Configuration configuration;

	public TransportationModule() {
		this.configuration = new Configuration(Minetopia.getPlugin(Minetopia.class), "modules/transportation.yml");
		this.configuration.load();

		this.portalManager = PortalManager.with(Minetopia.getPlugin(Minetopia.class).dataModule.getSqlHelper());
	}

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

		// Register Commands
		this.registerCommands();
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager commandManager = minetopia.getCommandManager();

		commandManager.registerCommand(new TransportationCommand(this.configuration));
	}

	private static final @Getter CooldownMap<Player> cooldownMap = CooldownMap.create(Cooldown.of(5, TimeUnit.SECONDS));

	private void registerEvents() {
		this.composite.bindModule(new TeleporterUseListener(this.configuration));
		this.composite.bindModule(new SignChangeListener(this.configuration));
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
		return "Transportation";
	}
}
