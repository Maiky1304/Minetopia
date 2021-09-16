package dev.maiky.minetopia.modules.bank.ui;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.bank.bank.Account;
import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.bank.bank.Permission;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoBankManager;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Numbers;
import lombok.Setter;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Door: Maiky
 * Info: Minetopia - 04 Jun 2021
 * Package: dev.maiky.minetopia.modules.bank.ui
 */

public class BalanceManageUI extends Gui {

	private final Account account;
	private final Economy economy = Minetopia.getEconomy();

	@Setter
	private boolean overrideRule = false;

	private final LinkedHashMap<Material, Double> MAPPING = new LinkedHashMap<>();

	private final MongoBankManager manager;

	private final OfflinePlayer target;

	public BalanceManageUI(Player player, Account account, OfflinePlayer target) {
		super(player, 6, Message.BANKING_GUI_BALANCE_TITLE.format((account == null ? target == null ? Numbers.convert(Numbers.Type.MONEY, Minetopia.getEconomy().getBalance(player))
				: Numbers.convert(Numbers.Type.MONEY, Minetopia.getEconomy().getBalance(target))
				: Numbers.convert(Numbers.Type.MONEY, account.getBalance()))));
		this.account = account;
		this.manager = DataModule.getInstance().getBankManager();
		this.target = target;

		MAPPING.put(Material.GHAST_TEAR, 5000d);
		MAPPING.put(Material.DIAMOND, 2500d);
		MAPPING.put(Material.REDSTONE, 1000d);
		MAPPING.put(Material.EMERALD, 500d);
		MAPPING.put(Material.IRON_INGOT, 100d);
		MAPPING.put(Material.QUARTZ, 50d);
		MAPPING.put(Material.COAL, 20d);
		MAPPING.put(Material.GOLD_INGOT, 10d);
		MAPPING.put(Material.GOLD_NUGGET, 1d);

		Events.subscribe(InventoryClickEvent.class)
				.filter(e -> e.getWhoClicked().getUniqueId().equals(getPlayer().getUniqueId()))
				.filter(e -> e.getRawSlot() < 36)
				.filter(e -> e.getCurrentItem() != null)
				.filter(e -> e.getCurrentItem().getType() != Material.AIR)
				.handler(e -> this.withdrawBig(e, e.getCurrentItem().getType(), e.getCurrentItem().getAmount()))
		.bindWith(this);
		Events.subscribe(InventoryClickEvent.class)
				.filter(e -> e.getWhoClicked().getUniqueId().equals(getPlayer().getUniqueId()))
				.filter(e -> e.getRawSlot() > 53)
				.filter(e -> e.getCurrentItem() != null)
				.filter(e -> e.getCurrentItem().getType() != Material.AIR)
				.handler(e -> this.depositBig(e, e.getCurrentItem().getType(), e.getCurrentItem().getAmount()))
		.bindWith(this);
	}

	private int availableItemSpace(Material material) {
		int total = 0;

		for (int i = 0; i < 36; i++) {
			ItemStack itemStack = getPlayer().getInventory().getItem(i);

			if (itemStack == null) total += 64;
			else {
				if (itemStack.getType() == material) {
					int available = (64 - itemStack.getAmount());
					total += available;
				}
			}
		}

		return total;
	}

	private final MenuScheme GLASS = new MenuScheme()
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("111111111")
			.mask("000000000");

	private final MenuScheme WITHDRAW_QUICK = new MenuScheme()
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("111111111");

	@Override
	public void redraw() {
		MenuPopulator populator = this.GLASS.newPopulator(this);
		while(populator.hasSpace()) populator.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).name(" ").buildItem().build());

		MenuPopulator money = this.WITHDRAW_QUICK.newPopulator(this);
		Item[] items = new Item[]{
				ItemStackBuilder.of(Material.GHAST_TEAR).name("&e€5.000")
				.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 5000, true)),

				ItemStackBuilder.of(Material.DIAMOND).name("&e€2.500")
						.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 2500, true)),

				ItemStackBuilder.of(Material.REDSTONE).name("&e€1.000")
						.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 1000, true)),

				ItemStackBuilder.of(Material.EMERALD).name("&e€500")
						.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 500, true)),

				ItemStackBuilder.of(Material.IRON_INGOT).name("&e€100")
						.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 100, true)),

				ItemStackBuilder.of(Material.QUARTZ).name("&e€50")
						.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 50, true)),

				ItemStackBuilder.of(Material.COAL).name("&e€20")
						.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 20, true)),

				ItemStackBuilder.of(Material.GOLD_INGOT).name("&e€10")
						.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 10, true)),

				ItemStackBuilder.of(Material.GOLD_NUGGET).name("&e€1")
						.lore("", "&7Linkerklik om dit biljet &e1x &7op te nemen.", "&7Rechterklik om dit biljet &emeerdere &7keren op te nemen.")
						.buildConsumer((click) -> withdraw(click, 1, true))
		};

		int k = 0;
		while(money.hasSpace()) {
			money.accept(items[k]);
			k++;
		}

		double balance = this.account == null ? target == null ? economy.getBalance(getPlayer()) : economy.getBalance(target) : manager.getAccount(this.account.getBank(), this.account.getAccountId()).getBalance();
		double addedToMenu = 0;

		while(balance >= 5000) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 5000;
			addedToMenu += 5000;
			this.getHandle().addItem(ItemStackBuilder.of(Material.GHAST_TEAR).name("&e€5.000")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}

		while(balance >= 2500) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 2500;
			addedToMenu += 2500;
			this.getHandle().addItem(ItemStackBuilder.of(Material.DIAMOND).name("&e€2.500")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}

		while(balance >= 1000) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 1000;
			addedToMenu += 1000;
			this.getHandle().addItem(ItemStackBuilder.of(Material.REDSTONE).name("&e€1.000")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}

		while(balance >= 500) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 500;
			addedToMenu += 500;
			this.getHandle().addItem(ItemStackBuilder.of(Material.EMERALD).name("&e€500")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}

		while(balance >= 100) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 100;
			addedToMenu += 100;
			this.getHandle().addItem(ItemStackBuilder.of(Material.IRON_INGOT).name("&e€100")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}

		while(balance >= 50) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 50;
			addedToMenu += 50;
			this.getHandle().addItem(ItemStackBuilder.of(Material.QUARTZ).name("&e€50")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}

		while(balance >= 20) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 20;
			addedToMenu += 20;
			this.getHandle().addItem(ItemStackBuilder.of(Material.COAL).name("&e€20")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}

		while(balance >= 10) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 10;
			addedToMenu += 10;
			this.getHandle().addItem(ItemStackBuilder.of(Material.GOLD_INGOT).name("&e€10")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}

		while(balance >= 1) {
			if ((addedToMenu / 5000) > 2304) break;

			balance -= 1;
			addedToMenu += 1;
			this.getHandle().addItem(ItemStackBuilder.of(Material.GOLD_NUGGET).name("&e€1")
					.lore("", "&7Linkerklik om dit &ebiljet &7op te nemen.").build());
		}
	}

	public void withdraw(InventoryClickEvent event, double amount, boolean mass) {
		if (!hasPermission(Permission.WITHDRAW)) {
			getPlayer().sendMessage(Message.BANKING_GUI_BALANCE_ERROR_MISSINGPERMISSIONS.format(Permission.WITHDRAW.getLabel()));
			return;
		}

		ItemStack clone = event.getCurrentItem().clone();

		ClickType type = event.getClick();

		if (type == ClickType.LEFT) {
			if (this.account == null) {
				OfflinePlayer local = target == null ? Bukkit.getOfflinePlayer(getPlayer().getUniqueId()) : target;

				if (!economy.has(local, amount)) {
					getPlayer().sendMessage(Message.COMMON_ERROR_NOTENOUGHMONEY.raw());
					return;
				}

				if (availableItemSpace(event.getCurrentItem().getType()) == 0) {
					getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
					return;
				}

				EconomyResponse response = economy.withdrawPlayer(local, amount);
				if (response.transactionSuccess()) {
					getPlayer().getInventory().addItem(getCash(amount, 1));
				}

				event.getWhoClicked().sendMessage(Message.BANKING_GUI_BALANCE_SUCCESS_WITHDRAW.format(Numbers.convert(Numbers.Type.MONEY, amount),
						Bank.PERSONAL.label.toLowerCase()));
			} else {
				if (manager.getAccount(this.account.getBank(), this.account.getAccountId()).getBalance() < amount) {
					getPlayer().sendMessage(Message.COMMON_ERROR_NOTENOUGHMONEY.raw());
					return;
				}

				if (availableItemSpace(event.getCurrentItem().getType()) == 0) {
					getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
					return;
				}

				if (manager.getAccount(this.account.getBank(), this.account.getAccountId()).withdraw(amount)) {

					getPlayer().getInventory().addItem(getCash(amount, 1));

					event.getWhoClicked().sendMessage(Message.BANKING_GUI_BALANCE_SUCCESS_WITHDRAW.format(Numbers.convert(Numbers.Type.MONEY, amount),
							this.account.getBank().label.toLowerCase()));
				}
			}

			new BalanceManageUI(getPlayer(), account == null ? null : manager.getAccount(this.account.getBank(), this.account.getAccountId()), this.target).open();
		} else if (type == ClickType.RIGHT && mass) {
			getPlayer().closeInventory();
			getPlayer().sendMessage(Message.BANKING_GUI_BALANCE_QUESTION_AMOUNT.raw());

			Events.subscribe(AsyncPlayerChatEvent.class)
					.filter(e -> e.getPlayer().equals(getPlayer()))
					.expireAfter(1)
					.expireAfter(30, TimeUnit.SECONDS)
					.handler(e -> {
						// Cancel Event
						e.setCancelled(true);

						// Grab Message
						String message = e.getMessage();

						// Do stuff with the message
						int items;
						try {
							items = Integer.parseInt(message);
						} catch (NumberFormatException exception) {
							new BalanceManageUI(getPlayer(), account == null ? null : manager.getAccount(this.account.getBank(), this.account.getAccountId()), this.target).open();
							getPlayer().sendMessage(Message.COMMON_ERROR_NOTANUMBER.raw());
							return;
						}

						if (this.account == null) {
							OfflinePlayer local = target == null ? Bukkit.getOfflinePlayer(getPlayer().getUniqueId()) : target;

							if (!economy.has(local, amount * items)) {
								new BalanceManageUI(getPlayer(), null, this.target).open();
								getPlayer().sendMessage(Message.COMMON_ERROR_NOTENOUGHMONEY.raw());
								return;
							}

							if (availableItemSpace(clone.getType()) < items) {
								new BalanceManageUI(getPlayer(), null, this.target).open();
								getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
								return;
							}

							EconomyResponse response = economy.withdrawPlayer(local, amount * items);
							if (response.transactionSuccess()) {
								getPlayer().getInventory().addItem(getCash(amount, items));
							}
						} else {
							if (manager.getAccount(this.account.getBank(), this.account.getAccountId()).getBalance() < (amount * items)) {
								new BalanceManageUI(getPlayer(), account, this.target).open();
								getPlayer().sendMessage(Message.COMMON_ERROR_NOTENOUGHMONEY.raw());
								return;
							}

							if (availableItemSpace(clone.getType()) < items) {
								getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
								return;
							}

							if (manager.getAccount(this.account.getBank(), this.account.getAccountId()).withdraw(amount * items)) {
								getPlayer().getInventory().addItem(getCash(amount, items));
							}
						}

						new BalanceManageUI(getPlayer(), account == null ? null : manager.getAccount(this.account.getBank(), this.account.getAccountId()), this.target).open();
					});
		}
	}


	private void depositBig(InventoryClickEvent event, Material material, int items) {
		if (!hasPermission(Permission.DEPOSIT)) {
			getPlayer().sendMessage(Message.BANKING_GUI_BALANCE_ERROR_MISSINGPERMISSIONS.format(Permission.DEPOSIT.getLabel()));
			return;
		}

		int itemsToGive = event.getClick() == ClickType.RIGHT ? 1 : items;

		if (!this.verifyLegitMoney(event.getCurrentItem().getItemMeta())) {
			event.getWhoClicked().sendMessage(Message.BANKING_GUI_BALANCE_COUNTERFEIT.raw());
			return;
		}

		if (this.account == null) {
			OfflinePlayer local = target == null ? Bukkit.getOfflinePlayer(getPlayer().getUniqueId()) : target;
			EconomyResponse response = economy.depositPlayer(local, MAPPING.get(material) * itemsToGive);

			if (response.transactionSuccess()) {
				if (itemsToGive == 1) {
					if (event.getCurrentItem().getAmount() == 1) {
						event.setCurrentItem(null);
					} else {
						event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
					}
				} else {
					event.setCurrentItem(null);
				}
			}

			event.getWhoClicked().sendMessage(Message.BANKING_GUI_BALANCE_SUCCESS_DEPOSIT.format(Numbers.convert(Numbers.Type.MONEY, MAPPING.get(material) * itemsToGive),
					Bank.PERSONAL.label.toLowerCase()));
		} else {
			manager.getAccount(this.account.getBank(), this.account.getAccountId()).deposit(MAPPING.get(material) * itemsToGive);

			if (itemsToGive == 1) {
				if (event.getCurrentItem().getAmount() == 1) {
					event.setCurrentItem(null);
				} else {
					event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
				}
			} else {
				event.setCurrentItem(null);
			}

			event.getWhoClicked().sendMessage(Message.BANKING_GUI_BALANCE_SUCCESS_DEPOSIT.format(Numbers.convert(Numbers.Type.MONEY, MAPPING.get(material) * itemsToGive),
					account.getBank().label.toLowerCase()));
		}

		new BalanceManageUI(getPlayer(), account == null ? null : manager.getAccount(this.account.getBank(), this.account.getAccountId()), this.target).open();
	}

	public void withdrawBig(InventoryClickEvent event, Material material, int items) {
		if (!hasPermission(Permission.WITHDRAW)) {
			getPlayer().sendMessage(Message.BANKING_GUI_BALANCE_ERROR_MISSINGPERMISSIONS.format(Permission.WITHDRAW.getLabel()));
			return;
		}

		int itemsToGive = event.getClick() == ClickType.RIGHT ? 1 : items;

		if (this.account == null) {
			OfflinePlayer local = target == null ? Bukkit.getOfflinePlayer(getPlayer().getUniqueId()) : target;

			if (!economy.has(local, MAPPING.get(material) * itemsToGive)) {
				getPlayer().sendMessage(Message.COMMON_ERROR_NOTENOUGHMONEY.raw());
				return;
			}

			if (availableItemSpace(material) < itemsToGive) {
				getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
				return;
			}

			EconomyResponse response = economy.withdrawPlayer(local, MAPPING.get(material) * itemsToGive);
			if (response.transactionSuccess()) {
				getPlayer().getInventory().addItem(getCash(MAPPING.get(material), itemsToGive));
			}
			event.getWhoClicked().sendMessage(Message.BANKING_GUI_BALANCE_SUCCESS_WITHDRAW.format(Numbers.convert(Numbers.Type.MONEY, MAPPING.get(material) * itemsToGive),
					Bank.PERSONAL.label.toLowerCase()));
		} else {
			if (manager.getAccount(this.account.getBank(), this.account.getAccountId()).getBalance() < MAPPING.get(material) * itemsToGive) {
				getPlayer().sendMessage(Message.COMMON_ERROR_NOTENOUGHMONEY.raw());
				return;
			}

			if (availableItemSpace(material) < itemsToGive) {
				getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
				return;
			}

			manager.getAccount(this.account.getBank(), this.account.getAccountId()).withdraw(MAPPING.get(material) * itemsToGive);
			getPlayer().getInventory().addItem(getCash(MAPPING.get(material), itemsToGive));
			event.getWhoClicked().sendMessage(Message.BANKING_GUI_BALANCE_SUCCESS_WITHDRAW.format(Numbers.convert(Numbers.Type.MONEY, MAPPING.get(material) * itemsToGive),
					this.account.getBank().label.toLowerCase()));
		}

		new BalanceManageUI(getPlayer(), account == null ? null : manager.getAccount(this.account.getBank(), this.account.getAccountId()), this.target).open();
	}

	private boolean hasPermission(Permission permission) {
		if (this.overrideRule) return true;
		if (this.account == null) return getPlayer().hasPermission("minetopia.common.privatebankaccount");

		List<Permission> list = this.manager.getAccount(this.account.getBank(), this.account.getAccountId()).getPermissions()
				.get(getPlayer().getUniqueId());
		if (list.contains(Permission.ALL)) return true;
		return list.contains(permission);
	}

	private boolean verifyLegitMoney(ItemMeta meta) {
		return meta.hasLore() && meta.getLore().contains(this.BANK_LORE);
	}

	protected final String BANK_LORE = "§cOfficiëel Minetopia Bankbiljet";

	public ItemStack[] getCash(double amount, int items) {
		Material material;
		if (amount == 5000) material = Material.GHAST_TEAR;
		else if (amount == 2500) material = Material.DIAMOND;
		else if (amount == 1000) material = Material.REDSTONE;
		else if (amount == 500) material = Material.EMERALD;
		else if (amount == 100) material = Material.IRON_INGOT;
		else if (amount == 50) material = Material.QUARTZ;
		else if (amount == 20) material = Material.COAL;
		else if (amount == 10) material = Material.GOLD_INGOT;
		else material = Material.GOLD_NUGGET;

		ItemStack[] itemStacks = new ItemStack[items];
		int j = 0;
		for (int i = 0; i < items; i++) {
			itemStacks[j] = ItemStackBuilder.of(material).name(Numbers.convert(Numbers.Type.MONEY, amount))
					.lore(BANK_LORE).build();
			j++;
		}

		return itemStacks;
	}

}