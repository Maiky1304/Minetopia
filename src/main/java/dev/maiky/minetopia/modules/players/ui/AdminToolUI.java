package dev.maiky.minetopia.modules.players.ui;

import dev.maiky.minetopia.modules.bank.ui.BankChooseUI;
import dev.maiky.minetopia.modules.colors.packs.ChatColor;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.players.ui.admintool.NameColorUI;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Door: Maiky
 * Info: Minetopia - 16 Jun 2021
 * Package: dev.maiky.minetopia.modules.players.ui
 */

public class AdminToolUI extends Gui {

	private final OfflinePlayer offlinePlayer;

	public AdminToolUI(Player player, OfflinePlayer offlinePlayer) {
		super(player, 3, "§3§lAdmin§b§lTool §8- §7" + offlinePlayer.getName());
		this.offlinePlayer = offlinePlayer;
	}

	private final MenuScheme MENU_SCHEME = new MenuScheme()
			.mask("000000000")
			.mask("010111110")
			.mask("000000000");

	@Override
	public void redraw() {
		Item[] items = new Item[]{
				ItemStackBuilder.of(Material.SKULL_ITEM).durability(SkullType.PLAYER.ordinal())
						.name("§b§l" + this.offlinePlayer.getName()).transformMeta(meta -> {
							SkullMeta meta1 = (SkullMeta) meta;
							meta1.setOwningPlayer(offlinePlayer);
						}).buildItem().build(),
				ItemStackBuilder.of(nbtFormat(ItemStackBuilder.of(Material.IRON_INGOT).name("§9Naamkleur")
						.lore("", "§7Verander de §bnaamkleur §7van &b" + offlinePlayer.getName() + "&7.").build(), ChatColor.CHATCOLOR_BOLD_AQUA.toString().toLowerCase()))
				.build(() -> new NameColorUI(getPlayer(), offlinePlayer).open()),
				ItemStackBuilder.of(Material.CHEST).name("§9Inventory")
				.lore("", "&7Open de &binventory &7van &b" + offlinePlayer.getName() + "&7.").build(() -> {
					if (!offlinePlayer.isOnline()) {
						getPlayer().sendMessage("§cDeze speler is offline!");
						return;
					}
					getPlayer().openInventory(offlinePlayer.getPlayer().getInventory());
					getPlayer().sendMessage("§6Je hebt nu de inventory geopend van §c" + offlinePlayer.getName() + "§6.");
				}),
				ItemStackBuilder.of(Material.FEATHER).name("&9Teleport")
				.lore("", "&7Teleporteer naar &b" + offlinePlayer.getName() + "&7.").build(() -> {
					if (!offlinePlayer.isOnline()) {
						getPlayer().sendMessage("§cDeze speler is offline!");
						return;
					}
					getPlayer().teleport(offlinePlayer.getPlayer());
					getPlayer().sendMessage("§6Je bent teleporteerd naar §c" + offlinePlayer.getName() + "§6.");
				}),
				ItemStackBuilder.of(Material.NAME_TAG).name("&9Level")
						.lore("", "&7Verander &b" + offlinePlayer.getName() + "'s &7Minetopia level.").build(() ->
				{
					this.close();
					getPlayer().sendMessage("§6Voer een nieuw §clevel §6in voor §c" + offlinePlayer.getName() + "§6.");
					Events.subscribe(AsyncPlayerChatEvent.class)
							.expireAfter(1)
							.filter(e -> e.getPlayer().equals(getPlayer()))
							.handler(e -> {
								e.setCancelled(true);

								if (e.getMessage().equals("annuleer")) {
									this.open();
									return;
								}

								int level;
								try {
									level = Integer.parseInt(e.getMessage());
								} catch (NumberFormatException exception) {
									getPlayer().sendMessage("§cDit is geen geldig getal!");
									return;
								}
								MinetopiaUser user;
								if (offlinePlayer.isOnline()) {
									user = MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId());
								} else {
									user = DataModule.getInstance().getPlayerManager().find(u -> u.getUuid().equals(offlinePlayer.getUniqueId()))
											.findFirst().orElse(null);
								}
								assert user != null;
								user.setLevel(level);
								if (!offlinePlayer.isOnline())
									DataModule.getInstance().getPlayerManager().save(user);
								getPlayer().sendMessage("§6Je hebt het level van §c" + offlinePlayer.getName() + " §6veranderd naar §cLevel " + level + "§6.");
								this.open();
							}).bindWith(this);
				}),
				ItemStackBuilder.of(Material.INK_SACK).durability(10).name("&9Bank")
						.lore("", "&7Open de bank van &b" + offlinePlayer.getName() + "&7.").build(() -> {
					BankChooseUI ui = new BankChooseUI(getPlayer(), offlinePlayer);
				})
		};

		int i = 0;
		MenuPopulator populator = this.MENU_SCHEME.newPopulator(this);
		while(populator.hasSpace()) {
			Item item = items[i];
			populator.accept(item);
			i++;
		}
	}

	private org.bukkit.inventory.ItemStack nbtFormat(org.bukkit.inventory.ItemStack itemStack, String string) {
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
		tagCompound.setString("mtcustom", string);
		nms.setTag(tagCompound);
		return CraftItemStack.asCraftMirror(nms);
	}

}
