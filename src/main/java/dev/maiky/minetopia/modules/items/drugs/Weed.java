package dev.maiky.minetopia.modules.items.drugs;

import dev.maiky.minetopia.modules.items.Interaction;
import dev.maiky.minetopia.modules.items.MinetopiaInteractable;
import dev.maiky.minetopia.util.Message;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Door: Maiky
 * Info: Minetopia - 06 Jun 2021
 * Package: dev.maiky.minetopia.modules.items.drugs
 */

public class Weed implements MinetopiaInteractable {

	@Override
	public Material material() {
		return Material.POISONOUS_POTATO;
	}

	@Override
	public int durability() {
		return 0;
	}

	@Override
	public String permission() {
		return "minetopia.common.consume.weed";
	}

	@Override
	public Interaction event() {
		return new Interaction() {
			@Override
			public void execute(PlayerInteractAtEntityEvent event) {
				// ignored
			}

			@Override
			public void execute(PlayerInteractEvent event) {
				event.getPlayer().sendMessage(Message.CONSUME_WEED.raw());
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 1));
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
				event.getItem().setAmount(event.getItem().getAmount() - 1);
			}
		};
	}
}
