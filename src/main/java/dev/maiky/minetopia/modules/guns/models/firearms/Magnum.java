package dev.maiky.minetopia.modules.guns.models.firearms;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models
 */

public class Magnum implements Model {

	@Override
	public double bulletVelocity() {
		return 450;
	}

	@Override
	public double bulletDamage() {
		return 8;
	}

	@Override
	public long delayBetweenShots() {
		return 1500;
	}

	@Override
	public String modelName() {
		return "magnum44_fullmodel";
	}

	@Override
	public int defaultAmmo() {
		return 6;
	}
}
