package dev.maiky.minetopia.modules.levels.ui;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.levels.ui
 */

public class LevelCheckUI extends Gui {

	private final MinetopiaUser user;

	public LevelCheckUI(Player player, Entity rightClicked) {
		super(player, 3, rightClicked.getCustomName());
		this.user = PlayerManager.getCache().get(player.getUniqueId());
	}

	private final MenuScheme ACCEPT = new MenuScheme()
			.mask("000000000")
			.mask("000100000")
			.mask("000000000");

	private final MenuScheme DENY = new MenuScheme()
			.mask("000000000")
			.mask("000001000")
			.mask("000000000");

	@Override
	public void redraw() {
		MenuPopulator populator = ACCEPT.newPopulator(this);
		while(populator.hasSpace())
			populator.accept(ItemStackBuilder.of(Material.WOOL)
			.durability(5).name("&a&lAkkoord").lore("", "&2Klik hier om je te upgraden naar &aLevel " + (user.getLevel() + 1) + "&2!",
							"&2Deze upgrade is &agratis&2.")
			.build(() ->
			{
				user.setLevel(user.getLevel() + 1);
				this.close();
				getPlayer().sendTitle("§6Gefeliciteerd!", "§7Je bent nu §eLevel " + user.getLevel() + "§7!"
				, 10, 50, 10);
			}));
		MenuPopulator populator1 = DENY.newPopulator(this);
		while(populator1.hasSpace())
			populator1.accept(ItemStackBuilder.of(Material.WOOL)
			.durability(14).name("&c&lAnnuleren").build(this::close));
	}

}
