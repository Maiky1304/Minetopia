package dev.maiky.minetopia.modules.prefixes.ui;

import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.prefixes.ui
 */

public class PrefixUI extends Gui {

	static int calculateSize(List<String> prefixes) {
		int len = prefixes.size();
		if (len > 9 && len < 18)
			return 18;
		if (len > 18 && len < 27)
			return 27;
		if (len > 27 && len < 36)
			return 36;
		if (len > 36 && len < 45)
			return 45;
		if (len > 45 && len < 54)
			return 54;
		return 9;
	}

	private final List<Item> prefixItems = new ArrayList<>();

	public PrefixUI(Player player, MinetopiaUser user){
		super(player, calculateSize(user.getPrefixes()) / 9, Message.PREFIX_GUI_TITLE.raw());
		this.prepare(user);
	}

	private void prepare(MinetopiaUser user) {
		this.prefixItems.add(ItemStackBuilder.of(Material.NAME_TAG)
		.name("&7" + user.getCurrentPrefix()).buildItem().build());

		for (String s : user.getPrefixes()) {
			if (s.equals(user.getCurrentPrefix()))continue;
			this.prefixItems.add(ItemStackBuilder.of(Material.PAPER)
					.name("&7" + s).build(() -> {
						getPlayer().sendMessage(Message.PREFIX_GUI_CHANGED.format(s));
						user.setCurrentPrefix(s);
						new PrefixUI(getPlayer(), user).open();
					}));
		}
	}

	@Override
	public void redraw() {
		this.addItems(this.prefixItems);
	}

}
