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

package dev.maiky.minetopia.modules.guns.listeners;

import dev.maiky.minetopia.modules.guns.GunsModule;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class DamageListener implements TerminableModule {

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(EntityDamageByEntityEvent.class)
				.filter(event -> event.getDamager() instanceof Snowball)
				.filter(event -> event.getDamager().getCustomName().startsWith("minetopia_bullet"))
				.filter(event -> event.getEntity() instanceof Player)
				.handler(event -> {
					Player gunman = (Player)((Snowball)event.getDamager()).getShooter();
					Player victim = (Player)event.getEntity();

					Model model = GunsModule.getInstance().getModel(event.getDamager().getCustomName().split(":")[1]);
					double damage = model.bulletDamage();

					if ((victim.getHealth() - damage) < 0) {
						victim.setHealth(0d);
					} else {
						victim.setHealth(victim.getHealth() - damage);
					}

					gunman.sendMessage(Message.GUNS_HIT_TOSHOOTER.format(victim.getName()));
					victim.sendMessage(Message.GUNS_HIT_TOVICTIM.format(gunman.getName()));
				})
				.bindWith(consumer);
	}

}
