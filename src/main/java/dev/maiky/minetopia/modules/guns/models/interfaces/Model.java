package dev.maiky.minetopia.modules.guns.models.interfaces;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models
 */

public interface Model {

	double bulletVelocity();
	double bulletDamage();

	long delayBetweenShots();

	boolean burst();

	String modelName();
	String customName();
	int defaultAmmo();

}
