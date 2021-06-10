package dev.maiky.minetopia.modules.upgrades.upgrades.handlers;

import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeFunction;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeStructure;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades.upgrades.handlers
 */

public class WalkSpeed implements UpgradeStructure {

	@Getter
	private final HashMap<Integer, Float> walkSpeed = new HashMap<>();

	public WalkSpeed() {
		walkSpeed.put(1, 0.2f);
		walkSpeed.put(2, 0.25f);
		walkSpeed.put(3, 0.3f);
		walkSpeed.put(4, 0.35f);
		walkSpeed.put(5, 0.385f);
	}

	@Override
	public String name() {
		return "Walkspeed";
	}

	@Override
	public UpgradeFunction upgradeFunction(int level) {
		return new UpgradeFunction() {
			@Override
			public void execute(Player p) {
				p.setWalkSpeed(walkSpeed.get(level));
			}
		};
	}

	@Override
	public Upgrade type() {
		return Upgrade.SPEED;
	}
}
