package dev.maiky.minetopia.modules.guns.models.firearms;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models
 */

public class Walther implements Model {

	@Override
	public double bulletVelocity() {
		return 408;
	}

	@Override
	public double bulletDamage() {
		return 6.5;
	}

	@Override
	public long delayBetweenShots() {
		return 1500;
	}

	@Override
	public String modelName() {
		return "waltherp99_fullmodel";
	}

	@Override
	public String customName() {
		return "Walther P99";
	}

	@Override
	public int defaultAmmo() {
		return 10;
	}
}
