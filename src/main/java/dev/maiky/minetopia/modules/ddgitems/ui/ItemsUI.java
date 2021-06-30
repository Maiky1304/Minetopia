package dev.maiky.minetopia.modules.ddgitems.ui;

import dev.maiky.minetopia.modules.ddgitems.items.ItemLoader;
import dev.maiky.minetopia.modules.ddgitems.items.classes.ItemData;
import dev.maiky.minetopia.modules.ddgitems.items.classes.ItemType;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 26 May 2021
 * Package: dev.maiky.minetopia.modules.ddgitems.ui
 */

public class ItemsUI extends Gui {

	private final ItemType type;
	private final int page;

	public ItemsUI(Player player, ItemType type, int page) {
		super(player, 6, Message.DDGITEMS_CATEGORY_TITLE.format((page + 1)));
		this.type = type;
		this.page = page;
		this.prepare();
	}

	private final MenuScheme PATTERN = new MenuScheme()
			.mask("111111111")
			.mask("111111111")
			.mask("111111111")
			.mask("111111111")
			.mask("000000000")
			.mask("000000000");

	private final MenuScheme GLASS = new MenuScheme()
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("111111111")
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

	private List<ItemData> use = new ArrayList<>();

	protected void prepare() {
		List<ItemData> itemDataList = ItemLoader.items;

		final List<ItemData> items = new ArrayList<>(),
				regular = new ArrayList<>(),
				custom = new ArrayList<>(),
				single = new ArrayList<>();

		for (ItemData itemData : itemDataList) {
			if (itemData.type == ItemType.ITEM)
				items.add(itemData);
			if (itemData.type == ItemType.REGULAR_ARMOR_SET)
				regular.add(itemData);
			if (itemData.type == ItemType.CUSTOM_ARMOR_SET)
				custom.add(itemData);
			if (itemData.type == ItemType.SINGLE_ARMOR_PIECE)
				single.add(itemData);
		}

		List<ItemData> list = this.type == ItemType.ITEM ? items :
				this.type == ItemType.REGULAR_ARMOR_SET ? regular :
						this.type == ItemType.CUSTOM_ARMOR_SET ? custom :
								single;
		use.addAll(list);
	}

	@Override
	public void redraw() {
		int pages = this.use.size() / 36;

		int start = page * 36;
		int max = start + 36;

		MenuPopulator populator = this.PATTERN.newPopulator(this);
		for (int j = start; j < max; j++) {
			if (!populator.hasSpace()) break;

			try {
				ItemData data = this.use.get(j);
				Object[] arr = data.createItem();
				ItemStack itemStack = (ItemStack) arr[0];
				List<ItemData> custom = (List<ItemData>) arr[1];

				populator.accept(ItemStackBuilder.of(itemStack).name(data.name).build(() ->
				{
					if (custom.size() == 0) {
						if (getPlayer().getInventory().firstEmpty() == -1) {
							getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
							return;
						}
						getPlayer().getInventory().addItem(itemStack);
					}else{
						if (getPlayer().getInventory().firstEmpty() == -1) {
							getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
							return;
						}

						getPlayer().getInventory().addItem(itemStack);

						for (ItemData data1 : custom) {
							if (getPlayer().getInventory().firstEmpty() == -1) {
								getPlayer().sendMessage(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
								break;
							}

							getPlayer().getInventory().addItem((ItemStack) data1.createItem()[0]);
						}
					}
				}));
			} catch (IndexOutOfBoundsException exception) {
				populator.accept(ItemStackBuilder.of(Material.AIR).buildItem().build());
			}
		}

		MenuPopulator glass = this.GLASS.newPopulator(this);
		while(glass.hasSpace())
			glass.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(7).name(" ").buildItem().build());

		MenuPopulator close = this.CLOSE.newPopulator(this);
		while(close.hasSpace())
			close.accept(ItemStackBuilder.of(Material.BARRIER).name(Message.COMMON_GUI_CLOSEMENU.format(ChatColor.RED)).build(this::close));

		MenuPopulator paginator = this.PAGINATION.newPopulator(this);
		for (int i = 0; i < 2; i ++) {
			if (i == 0) {
				if (this.page != 0 && this.page <= pages) {
					paginator.accept(ItemStackBuilder.of(Material.ARROW)
							.name(Message.COMMON_GUI_PAGEBACKWARDS.raw()).build(() -> new ItemsUI(getPlayer(), this.type, page - 1).open()));
				} else {
					paginator.accept(ItemStackBuilder.of(Material.AIR)
							.buildItem().build());
				}
			} else {
				if (this.page != pages) {
					paginator.accept(ItemStackBuilder.of(Material.ARROW)
							.name(Message.COMMON_GUI_PAGEFORWARD.raw()).build(() -> new ItemsUI(getPlayer(), this.type, page + 1).open()));
				} else {
					paginator.accept(ItemStackBuilder.of(Material.AIR)
							.buildItem().build());
				}
			}
		}
	}

}
