package dev.maiky.minetopia.modules.upgrades.upgrades.handlers;

import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeFunction;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeStructure;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes
 */

public class ElytraBoost implements UpgradeStructure {

	private CooldownMap<Player> cooldowns = CooldownMap.create(Cooldown.of(15, TimeUnit.SECONDS));

	public ElytraBoost(TerminableConsumer consumer) {
		Events.subscribe(PlayerInteractEvent.class)
				.filter(e -> MongoPlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> MongoPlayerManager.getCache().get(e.getPlayer().getUniqueId()).getMinetopiaUpgrades().getUpgrades().containsKey(Upgrade.ELYTRA))
				.filter(e -> MongoPlayerManager.getCache().get(e.getPlayer().getUniqueId()).getMinetopiaUpgrades().getUpgrades().get(Upgrade.ELYTRA) == 1)
				.filter(e -> e.getAction().toString().startsWith("LEFT_CLICK"))
				.filter(e -> e.getPlayer().isGliding())
				.filter(e -> {
					if (!cooldowns.test(e.getPlayer())) {
						e.getPlayer().sendMessage(Message.UPGRADES_ELYTRA_COOLDOWN.format(cooldowns.remainingTime(e.getPlayer(), TimeUnit.SECONDS)));
						return false;
					}
					return true;
				})
				.handler(e -> {
					e.getPlayer().setVelocity(e.getPlayer().getEyeLocation().getDirection().normalize().multiply(2d));
					e.getPlayer().sendMessage(Message.UPGRADES_ELYTRA_USED.raw());
				}).bindWith(consumer);
	}

	@Override
	public String name() {
		return "ElytraBoost";
	}

	@Override
	public UpgradeFunction upgradeFunction(int level) {
		return new UpgradeFunction() {
			@Override
			public void execute(Player empty) {}
		};
	}

	@Override
	public Upgrade type() {
		return Upgrade.ELYTRA;
	}
}
