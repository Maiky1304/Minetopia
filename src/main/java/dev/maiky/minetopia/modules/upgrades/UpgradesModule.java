package dev.maiky.minetopia.modules.upgrades;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUpgrades;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.upgrades.commands.UpgradeCommand;
import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.ElytraBoost;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.HealthBoost;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.JumpBoost;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.WalkSpeed;
import dev.maiky.minetopia.modules.upgrades.upgrades.handlers.classes.UpgradeStructure;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.BucketPartition;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades
 */

public class UpgradesModule implements MinetopiaModule {

	private CompositeTerminable composite = CompositeTerminable.create();

	private boolean enabled = false;

	@Getter
	private final int upgradesDefault;

	public UpgradesModule() {
		this.upgradesDefault = Minetopia.getPlugin(Minetopia.class).getConfiguration().get().getInt("player.default.upgrades.points");
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.enable();
		this.disable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		// Register Commands
		this.registerCommands();

		// Effects
		this.registerEffects();
	}

	@Getter
	private final HashMap<Upgrade, UpgradeStructure> effects = new HashMap<>();

	private void registerEffects() {
		UpgradeStructure[] upgradeStructures = new UpgradeStructure[]{
				new HealthBoost(),
				new JumpBoost(),
				new WalkSpeed(),
				new ElytraBoost(this.composite)
		};
		for (UpgradeStructure structure : upgradeStructures)
			effects.put(structure.type(), structure);

		Bucket<Player> bucket = BucketFactory.newHashSetBucket(20, PartitioningStrategies.lowestSize());

		Events.subscribe(PlayerJoinEvent.class).handler(e -> bucket.add(e.getPlayer())).bindWith(composite);
		Events.subscribe(PlayerQuitEvent.class).handler(e -> bucket.remove(e.getPlayer())).bindWith(composite);

		Schedulers.sync().runRepeating(() -> {
			BucketPartition<Player> part = bucket.asCycle().next();
			for (Player player : part) {
				MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());
				MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();
				for (Upgrade upgrade : upgrades.getUpgrades().keySet()) {
					if (upgrades.getUpgrades().get(upgrade) == 0) continue;
					UpgradeStructure structure = effects.get(upgrade);
					if (structure == null) continue;
					structure.upgradeFunction(upgrades.getUpgrades().get(upgrade)).execute(player);
				}
			}
		}, 1L, 1L).bindWith(composite);
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		minetopia.getCommandManager().registerCommand(new UpgradeCommand());
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			this.composite.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Upgrades";
	}
}
