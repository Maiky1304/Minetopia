package dev.maiky.minetopia.modules.bags.ui;

import dev.maiky.minetopia.modules.bags.bag.Bag;
import dev.maiky.minetopia.modules.bags.bag.BagType;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoBagManager;
import dev.maiky.minetopia.util.Options;
import dev.maiky.minetopia.util.SerializationUtils;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 29 May 2021
 * Package: dev.maiky.minetopia.modules.bags.ui
 */

public class KofferUI extends Gui {

	@Getter
	private int id;

	@Getter
	private int page;

	@Getter
	private ItemStack[] itemStacks;

	@Getter
	private MongoBagManager manager;

	private final MenuScheme ITEMS = new MenuScheme()
			.mask("111111111")
			.mask("111111111")
			.mask("111111111")
			.mask("111111111")
			.mask("111111111")
			.mask("000000000");

	private final MenuScheme GLASS = new MenuScheme()
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("011111110");

	private final MenuScheme PAGINATION = new MenuScheme()
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("000000000")
			.mask("100000001");

	public KofferUI(Player player, int id, ItemStack[] itemStacks, int page) {
		super(player, 6, "§3Koffer");
		this.id = id;
		this.page = page;
		this.itemStacks = itemStacks;
		this.manager = DataModule.getInstance().getBagManager();

		Schedulers.builder()
				.async()
				.afterAndEvery(10)
				.run(() -> {
					Bag bag = this.manager.find(b -> b.getBagId() == this.id)
							.findFirst().orElse(null);
					if (bag == null) {
						close();
						return;
					}
					this.itemStacks = SerializationUtils.itemStackArrayFromBase64(bag.getBase64Contents());
					this.redraw();
				})
				.bindWith(this);
		Events.subscribe(InventoryClickEvent.class)
		.filter(e -> e.getInventory().equals(getHandle()))
		.filter(e -> e.getRawSlot() >= getHandle().getSize())
		.handler(e -> {
			Bag bag = this.manager.find(b -> b.getBagId() == this.id)
					.findFirst().orElse(null);
			if (bag == null) {
				close();
				return;
			}

			if (itemStacks.length >= (bag.getRows() * 9)) {
				player.sendMessage("§cJe koffer is vol!");
				return;
			}

			final List<Material> materialList = new ArrayList<>();
			for (BagType value : BagType.values()) materialList.add(value.material);
			if (materialList.contains(e.getCurrentItem().getType()) && !Options.BAGS_STACK.asBoolean().get()) {
				net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(e.getCurrentItem());
				if (nms.getTag() != null) {
					if (nms.getTag().hasKey("id")) {
						player.sendMessage("§cJe kunt geen koffer in je koffer doen!");
						return;
					}
				}
			}

			List<ItemStack> itemStackList = new ArrayList<>();
			for (ItemStack it : this.itemStacks) {
				if (it == null) continue;
				if (it.getType() == Material.AIR) continue;
				itemStackList.add(it);
			}

			itemStackList.add(e.getCurrentItem());
			e.getWhoClicked().getInventory().setItem(e.getSlot(), null);

			this.itemStacks = new ItemStack[itemStackList.size()];
			for (int j = 0; j < itemStackList.size(); j++) {
				this.itemStacks[j] = itemStackList.get(j);
			}

			bag.setBase64Contents(SerializationUtils.itemStackArrayToBase64(this.itemStacks));
			this.manager.save(bag);

			KofferUI ui = new KofferUI(getPlayer(), this.id, this.itemStacks, this.page);
			ui.open();
		}).bindWith(this);
	}

	@Override
	public void redraw() {
		int pages = this.itemStacks.length / 45;

		int start = page * 45;
		int max = start + 45;

		MenuPopulator populator = this.ITEMS.newPopulator(this);
		for (int i = start; i < max; i++) {
			try {
				ItemStack itemStack = this.itemStacks[i];
				int finalI = i;
				populator.accept(ItemStackBuilder.of(itemStack).build(() -> {
					if (getPlayer().getInventory().firstEmpty() == -1) {
						getPlayer().sendMessage("§cJe hebt geen genoeg inventory ruimte hiervoor.");
						return;
					}

					this.itemStacks[finalI] = null;
					List<ItemStack> itemStackList = new ArrayList<>();
					for (ItemStack it : this.itemStacks) {
						if (it == null) continue;
						if (it.getType() == Material.AIR) continue;
						itemStackList.add(it);
					}
					this.itemStacks = new ItemStack[itemStackList.size()];
					for (int j = 0; j < itemStackList.size(); j++) {
						this.itemStacks[j] = itemStackList.get(j);
					}

					Bag bag = this.manager.find(b -> b.getBagId() == id).findFirst().orElse(null);
					if (bag == null) {
						close();
						return;
					}
					bag.setBase64Contents(SerializationUtils.itemStackArrayToBase64(this.itemStacks));
					this.manager.save(bag);

					getPlayer().getInventory().addItem(itemStack);

					KofferUI ui = new KofferUI(getPlayer(), this.id, this.itemStacks, this.page);
					ui.open();
				}));
			} catch (ArrayIndexOutOfBoundsException indexOutOfBoundsException) {
				populator.accept(ItemStackBuilder.of(Material.AIR).buildItem().build());
			}
		}

		Bag bag = this.manager.find(b -> b.getBagId() == this.id).findFirst().orElse(null);
		if (bag == null) {
			close();
			return;
		}

		int rows = bag.getRows();
		if (rows < 5) {
			int fill = (rows * 9);
			for(int j = fill; j < 54; j++) {
				setItem(j, ItemStackBuilder.of(Material.BARRIER).name(" ").buildItem().build());
			}
		}

		MenuPopulator glass = this.GLASS.newPopulator(this);
		while(glass.hasSpace())
			glass.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(7).name(" ").buildItem().build());

		MenuPopulator paginator = this.PAGINATION.newPopulator(this);
		for (int i = 0; i < 2; i ++) {
			if (i == 0) {
				if (this.page != 0 && this.page <= pages) {
					paginator.accept(ItemStackBuilder.of(Material.ARROW)
							.name("Vorige pagina").build(() -> new KofferUI(getPlayer(), this.id, this.itemStacks, this.page - 1).open()));
				} else {
					paginator.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(7).name(" ").buildItem().build());
				}
			} else {
				if (this.page != pages) {
					paginator.accept(ItemStackBuilder.of(Material.ARROW)
							.name("Volgende pagina").build(() -> new KofferUI(getPlayer(), this.id, this.itemStacks, this.page + 1).open()));
				} else {
					paginator.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(7).name(" ").buildItem().build());
				}
			}
		}
	}

}
