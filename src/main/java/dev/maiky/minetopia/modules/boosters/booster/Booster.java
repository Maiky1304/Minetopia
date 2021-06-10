package dev.maiky.minetopia.modules.boosters.booster;

import dev.maiky.minetopia.modules.boosters.booster.enums.BoosterType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 07 Jun 2021
 * Package: dev.maiky.minetopia.modules.boosters.boosters
 */

public class Booster {

	@Getter @Setter
	private int percentage;
	@Getter @Setter
	private BoosterType type;
	@Getter @Setter
	private String owner;

	public Booster(int percentage, BoosterType type, String owner) {
		this.percentage = percentage;
		this.type = type;
		this.owner = owner;
	}

}
