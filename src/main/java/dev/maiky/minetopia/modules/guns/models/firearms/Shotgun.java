package dev.maiky.minetopia.modules.guns.models.firearms;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.modules.guns.models.interfaces.Spread;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models.firearms
 */

@Spread(width = 8)
public class Shotgun implements Model {

	@Override
	public double bulletVelocity() {
		return 450;
	}

	@Override
	public double bulletDamage() {
		return 1.85;
	}

	@Override
	public long delayBetweenShots() {
		return 3500;
	}

	@Override
	public String modelName() {
		return "shotgun_fullmodel";
	}

	@Override
	public String customName() {
		return "Shotgun";
	}

	@Override
	public int defaultAmmo() {
		return 4;
	}
}
