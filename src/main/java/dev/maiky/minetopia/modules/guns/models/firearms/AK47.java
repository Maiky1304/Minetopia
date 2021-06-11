package dev.maiky.minetopia.modules.guns.models.firearms;

import dev.maiky.minetopia.modules.guns.models.interfaces.Burst;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models
 */

public class AK47 implements Model {

	@Override
	public double bulletVelocity() {
		return 900;
	}

	@Override
	public double bulletDamage() {
		return 9D;
	}

	@Override
	public long delayBetweenShots() {
		return 400;
	}

	@Override
	public String modelName() {
		return "ak47_fullmodel";
	}

	@Override
	public String customName() {
		return "AK-47";
	}

	@Override
	public int defaultAmmo() {
		return 30;
	}

}
