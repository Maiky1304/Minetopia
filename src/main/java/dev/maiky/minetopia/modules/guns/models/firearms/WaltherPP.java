package dev.maiky.minetopia.modules.guns.models.firearms;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models.firearms
 */

public class WaltherPP implements Model {

	@Override
	public double bulletVelocity() {
		return new Walther().bulletVelocity();
	}

	@Override
	public double bulletDamage() {
		return 6;
	}

	@Override
	public long delayBetweenShots() {
		return 600;
	}

	@Override
	public String modelName() {
		return "waltherpp_fullmodel";
	}

	@Override
	public String customName() {
		return "Walther PP";
	}

	@Override
	public int defaultAmmo() {
		return 10;
	}
}
