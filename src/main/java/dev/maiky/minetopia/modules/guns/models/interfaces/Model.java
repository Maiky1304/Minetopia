package dev.maiky.minetopia.modules.guns.models.interfaces;

import org.bukkit.ChatColor;

import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models
 */

public interface Model {

	double bulletVelocity();
	double bulletDamage();

	long delayBetweenShots();

	String modelName();
	int defaultAmmo();

}
