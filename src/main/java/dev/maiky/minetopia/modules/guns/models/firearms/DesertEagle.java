package dev.maiky.minetopia.modules.guns.models.firearms;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models
 */

public class DesertEagle implements Model {

	@Override
	public double bulletVelocity() {
		return 470;
	}

	@Override
	public double bulletDamage() {
		return 6.5d;
	}

	@Override
	public long delayBetweenShots() {
		return 2500;
	}

	@Override
	public String modelName() {
		return "deserteagle_fullmodel";
	}

	@Override
	public int defaultAmmo() {
		return 9;
	}

}
