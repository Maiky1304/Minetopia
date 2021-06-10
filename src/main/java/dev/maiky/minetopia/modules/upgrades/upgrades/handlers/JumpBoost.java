package dev.maiky.minetopia.modules.upgrades.upgrades.handlers;

import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeFunction;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeStructure;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades.upgrades.handlers
 */

public class JumpBoost implements UpgradeStructure {

	@Override
	public String name() {
		return "Jump";
	}

	@Override
	public UpgradeFunction upgradeFunction(int level) {
		return new UpgradeFunction() {
			@Override
			public void execute(Player p) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, (level - 1)), true);
			}
		};
	}

	@Override
	public Upgrade type() {
		return Upgrade.JUMP;
	}
}
