package dev.maiky.minetopia.modules.security;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.security.commands.BodySearchCommand;
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

	private static final List<Material> illegalItems = Arrays.asList(
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
		Events.subscribe(PlayerInteractEvent.class)
				.filter(e -> e.getAction() == Action.PHYSICAL)
				.filter(e -> e.getClickedBlock().getType().toString().endsWith("PLATE"))
				.filter(e -> e.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN)
				.getType().toString().contains("SIGN"))
				.handler(e -> {
					Player p = e.getPlayer();

					Sign sign = (Sign) e.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getState();
					if (!sign.getLine(0).equals("[DETECTOR]")) {
						return;
					}
					int radius = Integer.parseInt(sign.getLine(1));

					boolean carryingBag = false;
					boolean carryingIllegalItems = false;
					for (Material material : illegalItems) {
						if (p.getInventory().getItemInOffHand() != null) {
							if (p.getInventory().getItemInOffHand().getType() == material) {
								carryingIllegalItems = true;
								break;
							}
						}

						if (p.getInventory().contains(material)) {
							carryingIllegalItems = true;
							break;
						}
					}

					if (!carryingIllegalItems){

						if (p.getInventory().contains(Material.CARROT_STICK)) {
							carryingBag = true;
						}

					}

					List<Player> nearbyPlayers = new ArrayList<>();
					for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if (onlinePlayer.getLocation().distance(p.getLocation())
								<= 25) nearbyPlayers.add(onlinePlayer);
					}

					List<Block> blocks = getDetectionBlocks(e.getClickedBlock().getLocation(), radius);
					if (blocks.isEmpty())return;

					for(Block block : blocks) {
						for (Player nearbyPlayer : nearbyPlayers) {
							nearbyPlayer.sendBlockChange(block.getLocation(), Material.WOOL, (carryingIllegalItems ? (byte)14 : (carryingBag ? (byte)4 : 13)));
							Schedulers.sync().runLater(() -> {
								nearbyPlayer.sendBlockChange(block.getLocation(), Material.WOOL, (byte)15);
							}, 40).bindWith(composite);
						}
					}
				}).bindWith(composite);
	}

	private List<Block> getDetectionBlocks(Location location, int radius) {
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
