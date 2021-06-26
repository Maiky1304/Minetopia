package dev.maiky.minetopia.modules.security;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.security.commands.BodySearchCommand;
import dev.maiky.minetopia.modules.security.listeners.DetectorListener;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.security
 */

public class SecurityModule implements MinetopiaModule {

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

		// Events
		this.registerEvents();

		// Commands
		this.registerCommands();
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager manager = minetopia.getCommandManager();

		manager.getCommandConditions().addCondition(Player.class, "notBeingSearched", (context, execContext, value) -> {
			if ( BodySearchCommand.getBeingSearched().containsKey(value.getUniqueId()) ) throw new ConditionFailedException("Deze speler wordt al gefouilleerd.");
		});
		manager.registerCommand(new BodySearchCommand());
	}

	private static final @Getter List<Material> illegalItems = Arrays.asList(
			Material.SUGAR,
			Material.IRON_HOE,
			Material.STICK,
			Material.WOOD_SWORD,
			Material.SPIDER_EYE,
			Material.FERMENTED_SPIDER_EYE,
			Material.SNOW_BALL,
			Material.ARROW,
			Material.BOW,
			Material.ROTTEN_FLESH,
			Material.STONE_HOE,
			Material.POISONOUS_POTATO,
			Material.WOOD_HOE
	);

	public static boolean isIllegal(Material material) {
		return illegalItems.contains(material);
	}

	private void registerEvents() {
		this.composite.bindModule(new DetectorListener());
	}

	public static List<Block> getDetectionBlocks(Location location, int radius) {
		List<Block> blocks = new ArrayList<>();
		for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					if (location.getWorld().getBlockAt(x, y, z).getType() == Material.WOOL
							&& location.getWorld().getBlockAt(x, y, z).getData() == (byte)15)
						blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
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
		return "Security";
	}
}
