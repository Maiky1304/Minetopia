package dev.maiky.minetopia.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.util
 */

public class Items {

	public static boolean hasItemMatches(Player p, Material material, short durability) {
		boolean b = false;
		for (ItemStack itemStack : p.getInventory().getContents()) {
			if (itemStack != null)
				if (itemStack.getType() == material && itemStack.getDurability() == durability) {
					b = true;
					break;
				}
		}
		return b;
	}

}
