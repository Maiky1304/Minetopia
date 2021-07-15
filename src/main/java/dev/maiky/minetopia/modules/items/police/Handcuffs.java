package dev.maiky.minetopia.modules.items.police;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.items.Interaction;
import dev.maiky.minetopia.modules.items.MinetopiaInteractable;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Events;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static org.bukkit.potion.PotionEffectType.*;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons.police
 */

public class Handcuffs implements MinetopiaInteractable {

	public Handcuffs() {
		Events.subscribe(PlayerItemHeldEvent.class)
				.filter(e -> e.getPlayer().hasMetadata("cuffed"))
				.handler(e -> e.setCancelled(true));
		Events.subscribe(InventoryClickEvent.class)
				.filter(e -> e.getWhoClicked().hasMetadata("cuffed"))
				.handler(e -> e.setCancelled(true));
		Events.subscribe(PlayerTeleportEvent.class)
				.filter(e -> e.getPlayer().hasMetadata("cuffed"))
				.handler(e -> e.setCancelled(true));
		Events.subscribe(PlayerDropItemEvent.class)
				.filter(e -> e.getPlayer().hasMetadata("cuffed"))
				.handler(e -> e.setCancelled(true));
		Events.subscribe(PlayerJoinEvent.class)
				.filter(e -> !e.getPlayer().hasMetadata("cuffed"))
				.filter(e -> e.getPlayer().hasPotionEffect(BLINDNESS) && e.getPlayer().hasPotionEffect(SLOW) &&
						e.getPlayer().hasPotionEffect(REGENERATION) && e.getPlayer().hasPotionEffect(SLOW_DIGGING))
				.handler(e -> {
					e.getPlayer().removePotionEffect(SLOW_DIGGING);
					e.getPlayer().removePotionEffect(BLINDNESS);
					e.getPlayer().removePotionEffect(SLOW);
					e.getPlayer().removePotionEffect(REGENERATION);
				});
	}

	@Override
	public Material material() {
		return Material.INK_SACK;
	}

	@Override
	public int durability() {
		return 8;
	}

	@Override
	public String permission() {
		return "minetopia.job.police";
	}

	@Override
	public Interaction event() {
		return new Interaction() {
			@Override
			public void execute(PlayerInteractAtEntityEvent event) {
				Player player = event.getPlayer();
				Player entity = (Player) event.getRightClicked();

				List<MetadataValue> metadata = entity.getMetadata("cuffed");
				boolean cuffed = false;
				if (!metadata.isEmpty()) {
					MetadataValue value = metadata.iterator().next();
					if (value.value() instanceof Boolean)
						cuffed = value.asBoolean();
				}

				Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
				if (cuffed) {
					entity.removePotionEffect(SLOW_DIGGING);
					entity.removePotionEffect(BLINDNESS);
					entity.removePotionEffect(SLOW);
					entity.removePotionEffect(REGENERATION);

					entity.removeMetadata("cuffed", minetopia);
					entity.removeMetadata("cuffedBy", minetopia);

					entity.sendMessage(Message.ITEMS_POLICE_UNCUFF.format(player.getName()));
					player.sendMessage(Message.ITEMS_POLICE_UNCUFFEXEC.format(entity.getName()));
				} else {
					entity.removePotionEffect(SLOW_DIGGING);
					entity.removePotionEffect(BLINDNESS);
					entity.removePotionEffect(SLOW);
					entity.removePotionEffect(REGENERATION);
					entity.addPotionEffect(new PotionEffect(SLOW_DIGGING, Integer.MAX_VALUE, 0), true);
					entity.addPotionEffect(new PotionEffect(BLINDNESS, Integer.MAX_VALUE, 0), true);
					entity.addPotionEffect(new PotionEffect(SLOW, Integer.MAX_VALUE, 3), true);
					entity.addPotionEffect(new PotionEffect(REGENERATION, Integer.MAX_VALUE, 255), true);

					entity.setMetadata("cuffed", new FixedMetadataValue(minetopia, true));
					entity.setMetadata("cuffedBy", new FixedMetadataValue(minetopia, player.getUniqueId().toString()));

					entity.sendMessage(Message.ITEMS_POLICE_CUFF.format(player.getName()));
					player.sendMessage(Message.ITEMS_POLICE_CUFFEXEC.format(entity.getName()));

					BukkitRunnable runnable = new BukkitRunnable() {
						@Override
						public void run() {
							if (entity.hasMetadata("cuffed")) {
								if (!player.isOnline()) {
									this.cancel();
									return;
								}

								double d = 0.0D;
								try {
									d = player.getLocation().distance(entity.getLocation());
									if (d >= 12.5D)
										entity.teleport(player);
								} catch (Exception ignored) {}

								if (d >= 4.5D) {
									Vector direction = player.getLocation().toVector().subtract(entity.getLocation().toVector()).setY(0).multiply(2).normalize();
									entity.setVelocity(direction);
								}
							} else this.cancel();
						}
					};
					runnable.runTaskTimer(Minetopia.getPlugin(Minetopia.class), 0L, 5L);
				}
			}

			@Override
			public void execute(PlayerInteractEvent event) {}
		};
	}

}
