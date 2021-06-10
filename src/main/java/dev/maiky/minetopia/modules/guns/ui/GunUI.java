package dev.maiky.minetopia.modules.guns.ui;

import dev.maiky.minetopia.modules.guns.GunsModule;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.modules.guns.models.util.Builder;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Door: Maiky
 * Info: Minetopia - 10 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.ui
 */

public class GunUI extends Gui {

	private final GunsModule module;

	public GunUI(Player player, GunsModule module) {
		super(player, 4, "&0Model Menu");
		this.module = module;
	}

	private final MenuScheme menuScheme = new MenuScheme()
			.mask("111111111")
			.mask("100000001")
			.mask("100000001")
			.mask("111111111");
	
	private final MenuScheme models = new MenuScheme()
			.mask("000000000")
			.mask("011111110")
			.mask("011111110")
			.mask("000000000");

	@Override
	public void redraw() {
		MenuPopulator populator = menuScheme.newPopulator(this);
		while(populator.hasSpace())
			populator.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
			.durability(0).name(" ").buildItem().build());

		Item[] items = new Item[14];
		for (int i = 0; i < 14; i++) {
			try {
				Model model = module.getModels().get(i);
				items[i] = ItemStackBuilder.of(Builder.with(model).buildItem()).lore("", "&7Linkerklik &8om een wapen te maken",
						"&7Rechterklik &8om ammo te maken").buildConsumer((clickEvent) -> {
							if (clickEvent.getClick() == ClickType.LEFT)
								getPlayer().performCommand("guns get " + model.modelName());
							else getPlayer().performCommand("guns getammo " + model.modelName());
				});
			} catch (IndexOutOfBoundsException exception) {
				items[i] = ItemStackBuilder.of(Material.BARRIER).name(" ").buildItem().build();
			}
		}

		int i = 0;
		MenuPopulator modelPopulator = models.newPopulator(this);
		while(modelPopulator.hasSpace()) {
			modelPopulator.accept(items[i]);
			i++;
		}
	}

}
