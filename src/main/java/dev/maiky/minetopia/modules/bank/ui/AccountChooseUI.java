package dev.maiky.minetopia.modules.bank.ui;

import dev.maiky.minetopia.modules.bank.bank.Account;
import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoBankManager;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Door: Maiky
 * Info: Minetopia - 04 Jun 2021
 * Package: dev.maiky.minetopia.modules.bank.ui
 */

public class AccountChooseUI extends Gui {

	private final Bank bank;
	private final List<Account> accountList = new ArrayList<>();
	private final OfflinePlayer target;

	public AccountChooseUI(Player player, Bank bank, OfflinePlayer target) {
		super(player, 3, Message.BANKING_GUI_CHOOSEACCOUNT_TITLE.raw());

		this.bank = bank;
		this.target = target;

		if (bank == Bank.PERSONAL) return;

		MongoBankManager manager = DataModule.getInstance().getBankManager();
		accountList.addAll(this.target == null ? manager.find(account -> account.getBank().equals(bank) &&
				account.getPermissions().containsKey(player.getUniqueId())).collect(Collectors.toList())
				: manager.find(account -> account.getBank().equals(bank) && account.getPermissions().containsKey(this.target.getUniqueId())).collect(Collectors.toList()));
	}

	@Override
	public void redraw() {
		if (bank == Bank.PERSONAL) {
			super.addItem(ItemStackBuilder.of(bank.icon).name(target == null ? getPlayer().getName() : target.getName()).lore("§5(Privé rekening)").build(() -> account(null)));
		} else {
			for (Account account : this.accountList) {
				super.addItem(ItemStackBuilder.of(account.getBank().icon)
				.name(account.getCustomName()).lore("", "§5(ID: " + account.getAccountId() + ")").build(() -> account(account)));
			}
		}
	}

	private void account(Account account) {
		BalanceManageUI balanceManageUI = new BalanceManageUI(getPlayer(), account, this.target);
		if (this.target != null)
			balanceManageUI.setOverrideRule(true);
		balanceManageUI.open();
	}

}
