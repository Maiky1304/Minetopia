package dev.maiky.minetopia.modules.items.other;

import dev.maiky.minetopia.modules.items.Cooldownable;
import dev.maiky.minetopia.modules.items.MinetopiaWeapon;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons.other
 */

public class Cooldown {

	@Getter
	private MinetopiaWeapon weapon;

	@Getter
	private Date expiry;

	public Cooldown(MinetopiaUser user, MinetopiaWeapon weapon) {
		if (!(weapon instanceof Cooldownable)) return;
		this.weapon = weapon;

		Calendar calendar = Calendar.getInstance();
		int length = weapon.length();
		if (user.getMinetopiaUpgrades().getUpgrades().get(Upgrade.COOLDOWN) != 0) {
			length = length / 2;
		}
		calendar.add(weapon.unit(), length);
		this.expiry = calendar.getTime();
	}
}
