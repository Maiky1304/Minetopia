package dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes;

import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades.upgrades.handlers
 */

public interface UpgradeStructure {

	String name();
	UpgradeFunction upgradeFunction(int level);
	Upgrade type();

}
