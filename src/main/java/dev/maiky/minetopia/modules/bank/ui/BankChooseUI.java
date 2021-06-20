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
import org.bukkit.OfflinePlayer;
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
	private final OfflinePlayer target;

	public BankChooseUI(Player player, OfflinePlayer target) {
		super(player, 3, "&3Kies een rekeningsoort:");
		this.manager = BankManager.with(DataModule.getInstance().getSqlHelper());
		this.target = target;

		if (target == null)
		this.hasGovernment = this.manager.filterAndGet(Bank.GOVERNMENT, player.getUniqueId()).size() != 0;
		else this.hasGovernment = this.manager.filterAndGet(Bank.GOVERNMENT, target.getUniqueId()).size() != 0;
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
							} else {
								if (target != null) {
									if (!target.isOnline()) {
										getPlayer().sendMessage("§cJe kunt de persoonlijke rekening van §4" + target.getName() + " §cniet openen omdat hij/zij niet online is.");
										return;
									}
								}
							}

							AccountChooseUI accountChooseUI = new AccountChooseUI(getPlayer(), bank, target);
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
								if (target == null) {
									if ( this.manager.filterAndGet(bank, getPlayer().getUniqueId()).size() == 0 ) {
										getPlayer().sendMessage("§cJij hebt geen rekening in deze categorie.");
										return;
									}
								} else {
									if ( this.manager.filterAndGet(bank, target.getUniqueId()).size() == 0 ) {
										getPlayer().sendMessage("§4" + target.getName() + " heeft geen rekening in deze categorie.");
										return;
									}
								}
							} else {
								if (target != null) {
									if (!target.isOnline()) {
										getPlayer().sendMessage("§cJe kunt de persoonlijke rekening van §4" + target.getName() + " §cniet openen omdat hij/zij niet online is.");
										return;
									}
								}
							}

							AccountChooseUI accountChooseUI = new AccountChooseUI(getPlayer(), bank, target);
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
