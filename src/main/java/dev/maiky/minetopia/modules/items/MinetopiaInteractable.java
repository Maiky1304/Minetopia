package dev.maiky.minetopia.modules.items;

import org.bukkit.Material;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons
 */

public interface MinetopiaInteractable {

	Material material();
	int durability();
	String permission();
	Interaction event();

}
