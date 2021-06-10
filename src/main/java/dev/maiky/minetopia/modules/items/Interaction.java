package dev.maiky.minetopia.modules.items;

import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons
 */

public abstract class Interaction {

	public abstract void execute(PlayerInteractAtEntityEvent event);
	public abstract void execute(PlayerInteractEvent event);

}
