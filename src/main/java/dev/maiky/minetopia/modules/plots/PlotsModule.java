package dev.maiky.minetopia.modules.plots;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.plots.commands.PlotCommand;
import dev.maiky.minetopia.modules.plots.commands.PlotWandCommand;
import dev.maiky.minetopia.modules.plots.listener.PlotWandListener;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Options;
import lombok.Getter;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.plots
 */

public class PlotsModule implements MinetopiaModule {

	private final CompositeTerminable composite = CompositeTerminable.create();

	private boolean enabled = false;

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

		// WorldGuard
		this.registerWorldGuard();

		// Commands
		this.registerCommands();

		// Events
		this.registerEvents();
	}

	private void registerEvents() {
		this.composite.bindModule(new PlotWandListener());
	}

	@Getter
	private WorldGuardWrapper worldGuardWrapper;

	private void registerWorldGuard() {
		this.worldGuardWrapper = WorldGuardWrapper.getInstance();
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager bukkitCommandManager = minetopia.getCommandManager();

		bukkitCommandManager.getCommandConditions().addCondition("Plot", context -> {
			if (!context.getIssuer().isPlayer()) return;
			Player player = context.getIssuer().getPlayer();
			Location location = player.getLocation();
			Set<IWrappedRegion> regions = this.worldGuardWrapper.getRegions(location);
			List<IWrappedRegion> filtered = new ArrayList<>();
			for (IWrappedRegion wrappedRegion : regions) {
				if ( wrappedRegion.getPriority() >= Options.PLOTS_MINIMUMPRIORITY.asInt().get() ) filtered.add(wrappedRegion);
			}

			if (filtered.isEmpty()) {
				throw new ConditionFailedException(Message.PLOTS_ERROR_NOTONPLOT.raw());
			}
		});

		bukkitCommandManager.registerCommand(new PlotCommand(this.worldGuardWrapper));
		bukkitCommandManager.registerCommand(new PlotWandCommand());
	}

	@Override
	public void disable() {
		this.enabled = false;
	}

	@Override
	public String getName() {
		return "Plots";
	}
}
