package dev.maiky.minetopia.modules.bags.ui;

import dev.maiky.minetopia.modules.bags.bag.BagType;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Iterator;

/**
 * Door: Maiky
 * Info: Minetopia - 30 May 2021
 * Package: dev.maiky.minetopia.modules.bags.ui
 */

public class CreateUI extends Gui {

	private final MenuScheme scheme = new MenuScheme()
			.mask("111111111");

	public CreateUI(Player player) {
		super(player, 1, "§3Koffers");
	}

	@Override
	public void redraw() {
		Iterator<BagType> bagTypeIterator = BagType.createIterator();
		MenuPopulator populator = this.scheme.newPopulator(this);
		while(populator.hasSpace()) {
			BagType type = bagTypeIterator.next();
			populator.accept(ItemStackBuilder.of(type.create(-1))
					.build(() -> new RowsSelection(getPlayer(), type).open()));
		}

	}

	static class RowsSelection extends Gui {

		private final MenuScheme plus = new MenuScheme()
				.mask("000111000")
				.mask("000000000")
				.mask("000000000");

		private final MenuScheme minus = new MenuScheme()
				.mask("000000000")
				.mask("000000000")
				.mask("000111000");

		private final MenuScheme item = new MenuScheme()
				.mask("000000000")
				.mask("000010000")
				.mask("000000000");

		private final BagType type;

		private int total = 3;

		public RowsSelection(Player player, BagType type) {
			super(player, 3, "§3Hoeveel rows?");
			this.type = type;
		}

		@Override
		public void redraw() {
			int i = 1;
			int j = 1;

			MenuPopulator populator = this.plus.newPopulator(this);
			while(populator.hasSpace()) {
				int finalI = i;
				populator.accept(ItemStackBuilder.of(Material.STONE_BUTTON)
				.name(String.format("&a+%s", i)).build(() ->
						{
							total+= finalI;
							redraw();
						}));
				i++;
			}

			MenuPopulator populator2 = this.minus.newPopulator(this);
			while(populator2.hasSpace()) {
				int finalJ = j;
				populator2.accept(ItemStackBuilder.of(Material.STONE_BUTTON)
						.name(String.format("&c-%s", j)).build(() ->
						{
							if (total <= 0) return;

							total-= finalJ;
							redraw();
						}));
				j++;
			}

			this.item.newPopulator(this).accept(ItemStackBuilder.of(this.type.create(-1))
					.lore("", "&7Huidige rows: &a" + total, "",
							"&a&nKlik om de koffer te maken!").build(() ->
							getPlayer().performCommand("bag create " + type.toString() + " " + total)));
		}

	}

}
