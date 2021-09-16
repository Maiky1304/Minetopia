package dev.maiky.minetopia.modules.bank.ui;

import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoBankManager;
import dev.maiky.minetopia.util.Items;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Slot;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

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
	private final MongoBankManager manager;
	private final OfflinePlayer target;

	public BankChooseUI(Player player, @Nullable OfflinePlayer target) {
		super(player, 3, Message.BANKING_GUI_CHOOSETYPE_TITLE.raw());
		this.manager = DataModule.getInstance().getBankManager();
		this.target = target;

		if (target == null)
		this.hasGovernment = this.manager.find(account -> account.getBank().equals(Bank.GOVERNMENT) && account.getPermissions().containsKey(player.getUniqueId())).findFirst().isPresent();
		else this.hasGovernment = this.manager.find(account -> account.getBank().equals(Bank.GOVERNMENT) && account.getPermissions().containsKey(target.getUniqueId())).findFirst().isPresent();
	}

	@Override
	public void redraw() {
		if (this.hasGovernment) {
			MenuPopulator populator = this.four.newPopulator(this);
			int i = 0;
			while(populator.hasSpace()) {
				Bank bank = Bank.values()[i];
				populator.accept(ItemStackBuilder.of(Items.editNBT(ItemStackBuilder.of(bank.icon)
						.name(bank.color + bank.label + " Rekening").build(),
						"mtcustom", bank.nbtTag)).build(() ->
						{
							if (bank != Bank.PERSONAL) {
								if (!this.manager.find(account -> account.getBank().equals(bank) && account.getPermissions().containsKey(getPlayer().getUniqueId())).findFirst().isPresent() ) {
									getPlayer().sendMessage(Message.BANKING_GUI_CHOOSETYPE_ERROR_NOACCOUNT.raw());
									return;
								}
							} else {
								if (target != null) {
									if (!target.isOnline()) {
										getPlayer().sendMessage(Message.BANKING_GUI_CHOOSETYPE_ERROR_CANTOPENOTHERPERSONAL.format(target.getName()));
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
				populator.accept(ItemStackBuilder.of(Items.editNBT(ItemStackBuilder.of(bank.icon)
						.name(bank.color + bank.label + " Rekening").build(), "mtcustom", bank.nbtTag)).build(() ->
						{
							if (bank != Bank.PERSONAL) {
								if (target == null) {
									if ( !this.manager.find(account -> account.getBank().equals(bank) && account.getPermissions()
											.containsKey(getPlayer().getUniqueId())).findFirst().isPresent() ) {
										getPlayer().sendMessage(Message.BANKING_GUI_CHOOSETYPE_ERROR_NOACCOUNT.raw());
										return;
									}
								} else {
									if ( !this.manager.find(account -> account.getBank().equals(bank) && account.getPermissions().containsKey(target.getUniqueId())).findFirst().isPresent() ) {
										getPlayer().sendMessage(Message.BANKING_GUI_CHOOSETYPE_ERROR_NOACCOUNTOTHER.format(target.getName()));
										return;
									}
								}
							} else {
								if (target != null) {
									if (!target.isOnline()) {
										getPlayer().sendMessage(Message.BANKING_GUI_CHOOSETYPE_ERROR_CANTOPENOTHERPERSONAL.format(target.getName()));
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
