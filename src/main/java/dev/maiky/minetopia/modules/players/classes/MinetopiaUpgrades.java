package dev.maiky.minetopia.modules.players.classes;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.players.classes
 */

public class MinetopiaUpgrades {

	@Getter @Setter
	private int points;
	@Getter
	private final HashMap<Upgrade, Integer> upgrades = new HashMap<>();

	public MinetopiaUpgrades() {
		this.points = Minetopia.getPlugin(Minetopia.class).upgradesModule.getUpgradesDefault();
		for (Upgrade upgrade : Upgrade.values()) {
			upgrades.put(upgrade, 0);
		}
	}

}
