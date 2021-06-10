package dev.maiky.minetopia.modules.levels;

import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.levels.manager.LevelCheck;
import dev.maiky.minetopia.modules.levels.ui.LevelCheckUI;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Text;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.levels
 */

public class LevelsModule implements MinetopiaModule {

	private final CompositeTerminable composite = CompositeTerminable.create();
	private boolean enabled;

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

		// Events
		this.registerEvents();
	}

	private void registerEvents() {
		Events.subscribe(PlayerInteractAtEntityEvent.class)
				.filter(e -> e.getRightClicked().isCustomNameVisible())
				.filter(e -> e.getRightClicked().getCustomName() != null)
				.filter(e -> e.getHand() == EquipmentSlot.HAND)
				.filter(e -> Text.strip(e.getRightClicked().getCustomName()).equalsIgnoreCase("LevelCheck"))
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> {
					MinetopiaUser user = PlayerManager.getCache().get(e.getPlayer().getUniqueId());
					if ( new LevelCheck(user).calculatePossibleLevel() > user.getLevel() )
						return true;
					e.getPlayer().sendMessage("§cJe kan op dit moment niet naar een hoger level.");
					return false;
				})
				.handler(e -> new LevelCheckUI(e.getPlayer(), e.getRightClicked()).open())
		.bindWith(composite);
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
		return "Levels";
	}
}
