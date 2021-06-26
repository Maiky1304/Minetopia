package dev.maiky.minetopia.modules.boosters.booster;

import dev.maiky.minetopia.modules.boosters.enums.BoosterType;
import lombok.Getter;

/**
 * Door: Maiky
 * Info: Minetopia - 17 Jun 2021
 * Package: dev.maiky.minetopia.modules.boosters.booster.booster
 */

public class Booster {

	@Getter
	private final BoosterType type;

	@Getter
	private final int percentage;

	public Booster(BoosterType type, int percentage) {
		this.type = type;
		this.percentage = percentage;
	}

}
