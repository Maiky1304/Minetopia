package dev.maiky.minetopia.modules.bank.ui;

import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.BankManager;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Slot;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 30 May 2021
 * Package: dev.maiky.minetopia.modules.bank.ui
 */

public class BankChooseUI extends Gui {

	private final MenuScheme three = new MenuScheme()
			.mask("000000000")
			.mask("001010100")
			.mask("000000000");

	private final MenuScheme four = new MenuScheme()
			.mask("000000000")
			.mask("010101010")
			.mask("000000000");

	private final boolean hasGovernment;
	private final BankManager manager;

	public BankChooseUI(Player player) {
		super(player, 3, "&3Kies een rekeningsoort:");
		this.manager = BankManager.with(DataModule.getInstance().getSqlHelper());
		this.hasGovernment = this.manager.filterAndGet(Bank.GOVERNMENT, player.getUniqueId()).size() != 0;
	}

	@Override
	public void redraw() {
		if (this.hasGovernment) {
			MenuPopulator populator = this.four.newPopulator(this);
			int i = 0;
			while(populator.hasSpace()) {
				Bank bank = Bank.values()[i];
				populator.accept(ItemStackBuilder.of(bank.icon)
				.name(bank.color + bank.label + " Rekening").build(() ->
						{
							if (bank != Bank.PERSONAL) {
								if ( this.manager.filterAndGet(bank, getPlayer().getUniqueId()).size() == 0 ) {
									getPlayer().sendMessage("§cJij hebt geen rekening in deze categorie.");
									return;
								}
							}

							AccountChooseUI accountChooseUI = new AccountChooseUI(getPlayer(), bank);
							accountChooseUI.open();
						}));
				i++;
			}
		} else {
			MenuPopulator populator = this.three.newPopulator(this);
			int i = 0;
			while(populator.hasSpace()) {
				Bank bank = Bank.values()[i];
				populator.accept(ItemStackBuilder.of(bank.icon)
						.name(bank.color + bank.label + " Rekening").build(() ->
						{
							if (bank != Bank.PERSONAL) {
								if ( this.manager.filterAndGet(bank, getPlayer().getUniqueId()).size() == 0 ) {
									getPlayer().sendMessage("§cJij hebt geen rekening in deze categorie.");
									return;
								}
							}

							AccountChooseUI accountChooseUI = new AccountChooseUI(getPlayer(), bank);
							accountChooseUI.open();
						}));
				i++;
			}
		}

		for (int i = 0; i < 27; i++) {
			Slot slot = getSlot(i);
			if (slot.hasItem()) continue;
			slot.setItem(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).name(" ").build());
		}
	}

}
