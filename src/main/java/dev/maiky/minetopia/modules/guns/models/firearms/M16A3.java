package dev.maiky.minetopia.modules.guns.models.firearms;

import dev.maiky.minetopia.modules.guns.models.interfaces.Burst;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models
 */

@Burst
public class M16A3 implements Model {

	@Override
	public double bulletVelocity() {
		return 900;
	}

	@Override
	public double bulletDamage() {
		return 3D;
	}

	@Override
	public long delayBetweenShots() {
		return 400;
	}

	@Override
	public String modelName() {
		return "m16a3_fullmodel";
	}

	@Override
	public String customName() {
		return "M16A3";
	}

	@Override
	public int defaultAmmo() {
		return 30;
	}

}
