package dev.maiky.minetopia.modules.upgrades.upgrades;

import lombok.Getter;
import org.bukkit.Material;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades.upgrades
 */

public enum Upgrade {

	SPEED("Loopsnelheid", "&7Hiermee boost je je loopsnelheid.", 0.076f, 5),
	JUMP("Jump Boost", "&7Hiermee boost je jump hoogte.", 0.048f, 3),
	COOLDOWN("Wapenstok Cooldown", "&7Hiermee verminder je de cooldown van je wapenstok.", 0.08f, 1),
	ELYTRA("Elytra Boost", "&7Hiermee kun je elytra boosten om de 15 seconden\n&7doormiddel van linkermuisknop tijdens het vliegen.", 0.004f, 1),
	HEALTH("Extra Hartjes", "&7Hiermee krijg je extra hartjes bij je healthbar.", 0.044f, 3);

	@Getter
	private final String label, tag;

	@Getter
	private final int durability, max;

	Upgrade(String label, String tag, float a, int max) {
		this.label = label;
		this.tag = tag;
		this.durability = Math.round((a * Material.IRON_PICKAXE.getMaxDurability()));
		this.max = max;
	}

}
