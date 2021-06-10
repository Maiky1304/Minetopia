package dev.maiky.minetopia.modules.data.ui;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.data.ui
 */

public class ModuleUI extends Gui {

	private final MenuScheme GLASS = new MenuScheme()
			.mask("111111111")
			.mask("100000001")
			.mask("100000001")
			.mask("100000001")
			.mask("100000001")
			.mask("111111111");
	private final MenuScheme MODULES = new MenuScheme()
			.mask("000000000")
			.mask("011111110")
			.mask("011111110")
			.mask("011111110")
			.mask("011111110")
			.mask("000000000");

	private final List<MinetopiaModule> loadedModules = new ArrayList<>();

	public ModuleUI(Player player) {
		super(player, 6, "§3§lModules §8• §7" + Minetopia.getPlugin(Minetopia.class).getLoadedModules().size() +
				" geladen");

		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		HashMap<String, MinetopiaModule> hashMap = minetopia.getLoadedModules();
		loadedModules.addAll(hashMap.values());
	}

	@Override
	public void redraw() {
		MenuPopulator populator = GLASS.newPopulator(this);
		while(populator.hasSpace())
			populator.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(11).name(" ").buildItem().build());

		MenuPopulator populator1 = MODULES.newPopulator(this);
		int i = 0;
		while(populator1.hasSpace()) {
			try {
				MinetopiaModule module = loadedModules.get(i);
				populator1.accept(ItemStackBuilder.of(Material.EMPTY_MAP)
				.name(String.format("&b%s", module.getName())).lore("", "&7Status: " + (module.isEnabled() ? "&aEnabled" : "&cDisabled"), "", "&7Left click to reload module",
								"&7Right click to enable/disable module")
						.buildConsumer(event -> {
							ClickType type = event.getClick();
							if (type == ClickType.LEFT)
								module.reload();
							if (type == ClickType.RIGHT) {
								if (module.isEnabled())
									module.disable();
								else
									module.enable();
								this.redraw();
							}
						}));
			} catch (IndexOutOfBoundsException exception) {
				populator1.accept(ItemStackBuilder.of(Material.BARRIER).name(" ").buildItem().build());
			}
			i++;
		}
	}
}
