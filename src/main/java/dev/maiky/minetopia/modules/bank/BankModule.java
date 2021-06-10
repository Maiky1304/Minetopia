package dev.maiky.minetopia.modules.bank;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.bank.bank.Permission;
import dev.maiky.minetopia.modules.bank.commands.BankCommand;
import dev.maiky.minetopia.modules.bank.ui.BankChooseUI;
import dev.maiky.minetopia.util.Numbers;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Door: Maiky
 * Info: Minetopia - 30 May 2021
 * Package: dev.maiky.minetopia.modules.bank
 */

public class BankModule implements MinetopiaModule {

	private boolean enabled;
	private final CompositeTerminable composite = CompositeTerminable.create();

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.disable();
		this.enable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		// Events
		this.registerEvents();

		// Commands
		this.registerCommands();
	}

	private void registerCommands() {
		BukkitCommandManager manager = Minetopia.getPlugin(Minetopia.class).getCommandManager();

		manager.getCommandCompletions().registerStaticCompletion("bankTypes", Bank.list());
		manager.getCommandCompletions().registerStaticCompletion("bankPermissions", Permission.list());
		manager.getCommandConditions().addCondition(Bank.class, "noPrivate", (context, execContext, value) -> {
			if (value == Bank.PERSONAL) throw new ConditionFailedException("You are not allowed to use Private bank type!");
		});
		manager.registerCommand(new BankCommand());
	}

	private void registerEvents() {
		Events.subscribe(PlayerInteractEvent.class)
				.filter(PlayerInteractEvent::hasBlock)
				.filter(e -> e.getAction().toString().startsWith("RIGHT_CLICK"))
				.filter(e -> e.getClickedBlock().getType() == Material.RED_SANDSTONE_STAIRS)
				.handler(e -> {
					e.setCancelled(true);
					new BankChooseUI(e.getPlayer()).open();
				})
		.bindWith(composite);
		Events.subscribe(PlayerInteractEvent.class)
				.filter(PlayerInteractEvent::hasItem)
				.filter(this::isBankCard)
				.handler(e -> e.getPlayer().sendMessage("§6Banksaldo: §c" + Numbers.convert(Numbers.Type.MONEY, Minetopia.getEconomy().getBalance(e.getPlayer()))));
	}

	private boolean isBankCard(PlayerInteractEvent event) {
		if (event.getItem().getType() == Material.INK_SACK && event.getItem().getDurability() == 10)
			return true;
		return event.getItem().getType() == Material.DIAMOND_HOE && (event.getItem().getDurability() >= 656 &&
				event.getItem().getDurability() <= 661);
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
		return "Banking";
	}
}
