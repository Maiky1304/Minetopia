package dev.maiky.minetopia.modules.guns.models.firearms;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models
 */

public class Glock implements Model {

	@Override
	public double bulletVelocity() {
		return 380;
	}

	@Override
	public double bulletDamage() {
		return 5.5d;
	}

	@Override
	public long delayBetweenShots() {
		return 1500;
	}

	@Override
	public String modelName() {
		return "glock19_fullmodel";
	}

	@Override
	public String customName() {
		return "Glock 19";
	}

	@Override
	public int defaultAmmo() {
		return 15;
	}

}
