package dev.maiky.minetopia.modules.colors.gui;

import dev.maiky.minetopia.modules.colors.fonts.FontSet;
import dev.maiky.minetopia.modules.colors.packs.ChatColor;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.colors.gui
 */

public class ChatColorUI extends Gui {

	private final MinetopiaUser user;
	private final int page;

	private final MenuScheme DEFAULT_ICON = new MenuScheme()
			.mask("100000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000");

	private final MenuScheme PAGE_1 = new MenuScheme()
			.mask("000000000")
			.mask("000000000")
			.mask("111111111")
			.mask("111111111")
			.mask("111111111")
			.mask("000000000");

	private final MenuScheme PAGE_2 = new MenuScheme()
			.mask("111111111")
			.mask("111111111")
			.mask("111111111")
			.mask("111111110")
			.mask("000000000")
			.mask("000000000");

	private final MenuScheme CLOSE = new MenuScheme()
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000010000");

	private final MenuScheme PAGINATION = new MenuScheme()
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("100000001");

	public ChatColorUI(Player player, int page) {
		super(player, 6, Message.COLORS_GUI_CHATCOLOR_TITLE.setLimit(32).raw());
		this.page = page;
		this.user = PlayerManager.getCache().get(player.getUniqueId());

		this.prepare();
	}

	private final List<Item> colorItems = new ArrayList<>();

	private void prepare() {
		this.colorItems.add(ItemStackBuilder.of(nbtFormat(ItemStackBuilder.of(Material.IRON_INGOT)
				.name("§7Standaard chatkleur").lore("§a[Unlocked]").build(), ChatColor.CHATCOLOR_NORMAL_GRAY.toString().toLowerCase()))
		.build(() -> {
			this.user.setCurrentChatColor(ChatColor.CHATCOLOR_NORMAL_GRAY);
			getPlayer().sendMessage(Message.COLORS_GUI_COLORCHANGED.format("chat", "Standaard levelkleur"));
		}));

		for (ChatColor color : this.user.getChatColors().keySet()) {
			String expiry = this.user.getChatColors().get(color);
			boolean permanent = expiry.equals("-");

			List<String> lore;
			if (permanent) {
				lore = Message.COLORS_GUI_UNLOCKEDLORE_PERMANENT.formatAsList();
			} else lore = Message.COLORS_GUI_UNLOCKEDLORE_TEMPORARY.formatAsList(new SimpleDateFormat("dd/MM/yyyy HH:mm")
					.format(new Date(Long.parseLong(expiry))));

			Item item = ItemStackBuilder.of(nbtFormat(ItemStackBuilder.of(Material.IRON_INGOT)
					.name("§" + color.getColor() + (color.font ? FontSet.process(color.itemName) : color.itemName)).lore(lore).build(), color.toString().toLowerCase()))
					.build(() -> {
						this.user.setCurrentChatColor(color);
						getPlayer().sendMessage(Message.COLORS_GUI_COLORCHANGED.format("chat", "Standaard levelkleur"));
					});
			this.colorItems.add(item);
		}

		for (ChatColor color : ChatColor.values()) {
			if (this.user.getChatColors().containsKey(color)) continue;

			Item item = ItemStackBuilder.of(nbtFormat(ItemStackBuilder.of(Material.IRON_INGOT)
					.name("§" + color.getColor() + (color.font ? FontSet.process(color.itemName) : color.itemName)).lore("§c[Locked]").build(), color.toString().toLowerCase()))
					.buildItem().build();
			this.colorItems.add(item);
		}
	}

	@Override
	public void redraw() {
		if (page == 0) {
			DEFAULT_ICON.newPopulator(this)
					.accept(this.colorItems.get(0));

			MenuPopulator owned = this.PAGE_1.newPopulator(this);
			int i = 1;
			while(owned.hasSpace()) {
				owned.accept(this.colorItems.get(i));
				i++;
			}
		} else if (page == 1) {
			MenuPopulator owned = this.PAGE_2.newPopulator(this);
			int i = 28;
			while(owned.hasSpace()) {
				owned.accept(this.colorItems.get(i));
				i++;
			}
		}

		MenuPopulator paginator = this.PAGINATION.newPopulator(this);
		if (page == 0) {
			paginator.accept(ItemStackBuilder.of(Material.AIR).buildItem().build());
			paginator.accept(ItemStackBuilder.of(Material.ARROW).name("Pagina vooruit")
			.build(() -> new ChatColorUI(getPlayer(), 1).open()));
		}

		if (page == 1) {
			paginator.accept(ItemStackBuilder.of(Material.ARROW).name("Pagina terug")
					.build(() -> new ChatColorUI(getPlayer(), 0).open()));
			paginator.accept(ItemStackBuilder.of(Material.AIR).buildItem().build());
		}

		this.CLOSE.newPopulator(this)
				.accept(ItemStackBuilder.of(Material.BARRIER).name("&cSluit het menu").build(this::close));
	}

	private ItemStack nbtFormat(ItemStack itemStack, String string) {
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
		tagCompound.setString("mtcustom", string);
		nms.setTag(tagCompound);
		return CraftItemStack.asCraftMirror(nms);
	}

}
