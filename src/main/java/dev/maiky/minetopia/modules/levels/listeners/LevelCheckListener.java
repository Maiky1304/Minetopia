/*
 * This file is part of Minetopia.
 *
 *  Copyright (c) Maiky1304 (Maiky) <maiky@blackmt.nl>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package dev.maiky.minetopia.modules.levels.listeners;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.levels.manager.LevelCheck;
import dev.maiky.minetopia.modules.levels.ui.LevelCheckUI;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class LevelCheckListener implements TerminableModule {

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerInteractAtEntityEvent.class)
				.filter(e -> e.getRightClicked().isCustomNameVisible())
				.filter(e -> e.getRightClicked().getCustomName() != null)
				.filter(e -> e.getHand() == EquipmentSlot.HAND)
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> Text.strip(e.getRightClicked().getCustomName()).equalsIgnoreCase("LevelCheck"))
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> {
					MinetopiaUser user = PlayerManager.getCache().get(e.getPlayer().getUniqueId());
					if ( new LevelCheck(user).calculatePossibleLevel() > user.getLevel() )
						return true;
					e.getPlayer().sendMessage(Message.COMMON_ERROR_LEVELUP.raw());
					return false;
				})
				.handler(e -> new LevelCheckUI(e.getPlayer(), e.getRightClicked()).open())
				.bindWith(consumer);
	}

}
