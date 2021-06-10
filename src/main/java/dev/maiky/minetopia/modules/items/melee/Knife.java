package dev.maiky.minetopia.modules.items.melee;

import dev.maiky.minetopia.modules.items.MinetopiaWeapon;
import org.bukkit.Material;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons.melee
 */

public class Knife implements MinetopiaWeapon {

	@Override
	public Material material() {
		return Material.WOOD_SWORD;
	}

	@Override
	public String name() {
		return "Mes";
	}

	@Override
	public String damagerMessage() {
		return "&cJe hebt &4%s &cgestoken met een mes.";
	}

	@Override
	public String victimMessage() {
		return "&cJe bent door &4%s &cgestoken met een mes.";
	}

	@Override
	public boolean damage() {
		return true;
	}

}
