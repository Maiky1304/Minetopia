package dev.maiky.minetopia.modules.bank;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.bank.bank.Permission;
import dev.maiky.minetopia.modules.bank.commands.BankCommand;
import dev.maiky.minetopia.modules.bank.commands.PinCommand;
import dev.maiky.minetopia.modules.bank.listeners.ATMInteractListener;
import dev.maiky.minetopia.modules.bank.listeners.DebitCardListener;
import dev.maiky.minetopia.modules.bank.manager.PinManager;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Message;
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

	private Configuration configuration;
	private PinManager pinManager;

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

		// Config
		this.configuration = new Configuration(Minetopia.getPlugin(Minetopia.class), "modules/pinconsoles.yml");
		this.configuration.load();

		// Pin Manager
		this.pinManager = new PinManager(this.configuration);

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
			if (value == Bank.PERSONAL) throw new ConditionFailedException(Message.BANKING_ERROR_NOPRIVATE.raw());
		});
		manager.registerCommand(new BankCommand(this.pinManager));
		manager.registerCommand(new PinCommand(this.pinManager));
	}

	private void registerEvents() {
		this.composite.bindModule(new ATMInteractListener());
		this.composite.bindModule(new DebitCardListener());
	}

	public static boolean isBankCard(PlayerInteractEvent event) {
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
