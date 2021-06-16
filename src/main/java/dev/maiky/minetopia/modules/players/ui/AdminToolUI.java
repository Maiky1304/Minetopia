package dev.maiky.minetopia.modules.players.ui;

import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 16 Jun 2021
 * Package: dev.maiky.minetopia.modules.players.ui
 */

public class AdminToolUI extends Gui {

	public AdminToolUI(Player player) {
		super(player, 3, "§3Admin§bTool");
	}

	@Override
	public void redraw() {

	}

}
