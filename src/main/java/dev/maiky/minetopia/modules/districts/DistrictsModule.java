package dev.maiky.minetopia.modules.districts;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.districts.commands.DistrictsCommand;
import dev.maiky.minetopia.modules.players.PlayersModule;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Text;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.areas
 */

public class DistrictsModule implements MinetopiaModule {

	@Getter
	private final Configuration configuration;

	public DistrictsModule() {
		this.configuration = new Configuration(Minetopia.getPlugin(Minetopia.class), "modules/districts.yml");
		this.configuration.load();
	}

	private CompositeTerminable composite = CompositeTerminable.create();

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

		// Commands
		this.registerCommands();

		// Events
		this.registerEvents();

		// Cache
		this.initializeCache();
	}

	public void initializeCache() {
		blockCache.clear();

		for (String key : this.configuration.get().getKeys(false)) {
			if (!this.configuration.get().contains(key + ".color")) {
				blockCache.put(this.configuration.get().getString(key + ".name"), PlayersModule.getInstance().getCityColor());
			} else {
				blockCache.put(this.configuration.get().getString(key + ".name"),
						this.configuration.get().getString(key + ".color"));
			}
		}
	}

	@Getter
	private static final HashMap<UUID, String> locationCache = new HashMap<>();

	@Getter
	private static final HashMap<String, String> blockCache = new HashMap<>();

	private void registerEvents() {
		Events.subscribe(PlayerJoinEvent.class, EventPriority.LOWEST)
				.handler(e -> locationCache.put(e.getPlayer().getUniqueId(), getCurrentLocation(e.getPlayer())))
				.bindWith(composite);

		Events.subscribe(PlayerQuitEvent.class)
				.handler(e -> locationCache.remove(e.getPlayer().getUniqueId()))
				.bindWith(composite);

		Events.subscribe(PlayerMoveEvent.class)
				.filter(e -> e.getFrom().distanceSquared(e.getFrom()) >= 0)
				.handler(e -> {
					String current = getCurrentLocation(e.getPlayer());
					if (!current.equals(locationCache.get(e.getPlayer().getUniqueId()))) {
						locationCache.put(e.getPlayer().getUniqueId(), current);
						PlayerManager.getScoreboard().get(e.getPlayer().getUniqueId()).initialize();
						String color = getLocationCache().get(e.getPlayer().getUniqueId()) == null ? PlayersModule.getInstance().getCityColor()
								: getBlockCache().get(getLocationCache().get(e.getPlayer().getUniqueId()));
						e.getPlayer().sendTitle(Text.colors("&"
						+ color + "Welkom in"), Text.colors("&" + color
								+ current), 20, 50, 20);
					}
				})
				.bindWith(composite);
	}

	private String getCurrentLocation(Player player) {
		Configuration configuration = Minetopia.getPlugin(Minetopia.class)
				.districtsModule.getConfiguration();
		Location location = player.getLocation();
		location.setY(0);
		Block b = location.getBlock();
		if (b == null)
			return PlayersModule.getInstance().getCityName();
		if (b.getType() == Material.AIR)
			return PlayersModule.getInstance().getCityName();
		if (!configuration.get().contains(b.getType().toString()))
			return PlayersModule.getInstance().getCityName();
		return configuration.get().getString(b.getType().toString() + ".name");
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager commandManager = minetopia.getCommandManager();

		commandManager.getCommandConditions().addCondition(Player.class, "lookingAtBlock", (context, execContext, value) -> {
			if (value.getTargetBlock(null, 50) == null) {
				throw new ConditionFailedException("Je kijkt niet naar een blok!");
			}
		});

		commandManager.getCommandConditions().addCondition(String.class, "validateMaterial", (context, execContext, value) -> {
			try {
				Material.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException exception) {
				throw new ConditionFailedException("Dit is geen geldig block type!");
			}
		});

		commandManager.getCommandCompletions().registerCompletion("existingDistricts", context -> new ArrayList<>(this.configuration.get().getKeys(false)));

		commandManager.registerCommand(new DistrictsCommand(this.configuration));
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
		return "Districts";
	}
}
