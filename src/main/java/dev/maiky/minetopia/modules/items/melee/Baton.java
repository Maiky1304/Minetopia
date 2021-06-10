package dev.maiky.minetopia.modules.items.melee;

import dev.maiky.minetopia.modules.items.Cooldownable;
import dev.maiky.minetopia.modules.items.MinetopiaWeapon;
import org.bukkit.Material;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons.melee
 */

public class Baton implements MinetopiaWeapon, Cooldownable {

	@Override
	public Material material() {
		return Material.FERMENTED_SPIDER_EYE;
	}

	@Override
	public String name() {
		return "Wapenstok";
	}

	@Override
	public String damagerMessage() {
		return "&cJe hebt &4%s &cgeslagen met een wapenstok.";
	}

	@Override
	public String victimMessage() {
		return "&cJe bent door &4%s &cgeslagen met een wapenstok.";
	}

	@Override
	public int length() {
		return 2000;
	}

	@Override
	public int unit() {
		return 14;
	}

	@Override
	public String cooldownMessage() {
		return "&cJe moet nog even wachten voordat je je wapenstok weer kunt gebruiken.";
	}

	@Override
	public boolean damage() {
		return false;
	}
}
