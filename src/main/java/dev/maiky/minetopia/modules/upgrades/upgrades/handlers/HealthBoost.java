package dev.maiky.minetopia.modules.upgrades.upgrades.handlers;

import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeFunction;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeStructure;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes
 */

public class HealthBoost implements UpgradeStructure {

	@Override
	public String name() {
		return "HealthBoost";
	}

	@Override
	public UpgradeFunction upgradeFunction(int level) {
		return new UpgradeFunction() {
			@Override
			public void execute(Player p) {
				p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20d + (2 * level));
			}
		};
	}

	@Override
	public Upgrade type() {
		return Upgrade.HEALTH;
	}
}
