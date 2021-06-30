package dev.maiky.minetopia.modules.districts;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.districts.commands.DistrictsCommand;
import dev.maiky.minetopia.modules.districts.listeners.JoinListener;
import dev.maiky.minetopia.modules.districts.listeners.MovementListener;
import dev.maiky.minetopia.modules.districts.listeners.QuitListener;
import dev.maiky.minetopia.modules.players.PlayersModule;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Message;
import lombok.Getter;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
		this.composite.bindModule(new JoinListener());
		this.composite.bindModule(new QuitListener());
		this.composite.bindModule(new MovementListener());
	}

	public static String getCurrentLocation(Player player) {
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
				throw new ConditionFailedException(Message.COMMON_ERROR_NOTLOOKINGATBLOCK.raw());
			}
		});

		commandManager.getCommandConditions().addCondition(String.class, "validateMaterial", (context, execContext, value) -> {
			try {
				Material.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException exception) {
				throw new ConditionFailedException(Message.COMMON_ERROR_INVALIDBLOCKTYPE.raw());
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
