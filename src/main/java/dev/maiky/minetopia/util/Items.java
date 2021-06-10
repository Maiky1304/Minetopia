package dev.maiky.minetopia.util;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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

	public static boolean containsItem(Inventory inventory, ItemStack stack) {
		boolean b = false;
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item
					.getItemMeta() != null && item
					.getItemMeta().getDisplayName() != null && item.getItemMeta().getLore() != null) {
				if (item.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName()) && item
						.getItemMeta().getLore().equals(stack.getItemMeta().getLore()))
					b = true;
				break;
			}
		}
		return b;
	}

	public static ItemStack editNBT(ItemStack itemStack, String key, String value) {
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = nmsStack.getTag() == null ? new NBTTagCompound() : nmsStack.getTag();
		compound.setString(key, value);
		nmsStack.setTag(compound);
		itemStack = CraftItemStack.asCraftMirror(nmsStack);
		return itemStack;
	}

}
