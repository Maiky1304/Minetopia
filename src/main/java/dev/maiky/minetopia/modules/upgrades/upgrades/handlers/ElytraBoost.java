package dev.maiky.minetopia.modules.upgrades.upgrades.handlers;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeFunction;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeStructure;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
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
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> PlayerManager.getCache().get(e.getPlayer().getUniqueId()).getMinetopiaUpgrades().getUpgrades().containsKey(Upgrade.ELYTRA))
				.filter(e -> PlayerManager.getCache().get(e.getPlayer().getUniqueId()).getMinetopiaUpgrades().getUpgrades().get(Upgrade.ELYTRA) == 1)
				.filter(e -> e.getAction().toString().startsWith("LEFT_CLICK"))
				.filter(e -> e.getPlayer().isGliding())
				.filter(e -> {
					if (!cooldowns.test(e.getPlayer())) {
						e.getPlayer().sendMessage("§cJe moet nog §4" + cooldowns.remainingTime(e.getPlayer(), TimeUnit.SECONDS) + " §cseconden wachten voordat je je Elytra Boost weer kunt gebruiken.");
						return false;
					}
					return true;
				})
				.handler(e -> {
					e.getPlayer().setVelocity(e.getPlayer().getEyeLocation().getDirection().normalize().multiply(2d));
					e.getPlayer().sendMessage("§6Je hebt je Elytra Boost gebruikt van je upgrade, je hebt nu §c15 §6seconden cooldown.");
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
