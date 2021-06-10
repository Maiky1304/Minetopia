package dev.maiky.minetopia.modules.items.police;

import dev.maiky.minetopia.modules.items.Interaction;
import dev.maiky.minetopia.modules.items.MinetopiaInteractable;
import dev.maiky.minetopia.modules.items.displays.RadioUI;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.items.police
 */

public class Radio implements MinetopiaInteractable {

	@Override
	public Material material() {
		return Material.DIAMOND_HOE;
	}

	@Override
	public int durability() {
		return 67;
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
			}

			@Override
			public void execute(PlayerInteractEvent event) {
				RadioUI radioUI = new RadioUI(event.getPlayer());
				radioUI.open();
			}
		};
	}
}
