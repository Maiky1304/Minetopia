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

import dev.maiky.minetopia.modules.data.managers.WeaponManager;
import dev.maiky.minetopia.modules.guns.GunsModule;
import dev.maiky.minetopia.modules.guns.gun.Weapon;
import dev.maiky.minetopia.modules.guns.models.interfaces.Burst;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.modules.guns.models.interfaces.Spread;
import dev.maiky.minetopia.modules.guns.models.util.Builder;
import dev.maiky.minetopia.modules.notifications.util.NotificationUtil;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TriggerListener implements TerminableModule {

	private final WeaponManager weaponManager;
	private final List<Player> reloading;
	private final HashMap<Model, CooldownMap<String>> cooldowns;

	public TriggerListener(WeaponManager weaponManager, List<Player> reloading, HashMap<Model, CooldownMap<String>> cooldowns) {
		this.weaponManager = weaponManager;
		this.reloading = reloading;
		this.cooldowns = cooldowns;
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerInteractEvent.class)
				.filter(PlayerInteractEvent::hasItem)
				.filter(e -> e.getAction().toString().startsWith("RIGHT_CLICK"))
				.filter(e -> e.getItem().getType() == Material.WOOD_HOE)
				.filter(e -> CraftItemStack.asNMSCopy(e.getItem()).getTag() != null)
				.filter(e -> Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getItem()).getTag()).hasKey("mtcustom"))
				.filter(e -> !reloading.contains(e.getPlayer()))
				.handler(e -> {
					Player player = e.getPlayer();

					NBTTagCompound nbtTagCompound = Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand())
							.getTag());
					Model model = GunsModule.getInstance().getModel(nbtTagCompound.getString("mtcustom"));
					String license = nbtTagCompound.getString("license");
					Weapon weapon = weaponManager.getWeaponByLicense(license);

					ItemStack ammoItem = Builder.with(model).buildAmmo();

					if (weapon.getAmmo() == 0) {
						if (!player.getInventory().containsAtLeast(ammoItem, 1)) {
							player.sendMessage(Message.GUNS_NOAMMO.raw());
							return;
						}

						int j = -1;
						for (int k = 0; k < player.getInventory().getSize(); k++) {
							if (player.getInventory().getItem(k) == null) continue;
							ItemStack clone = player.getInventory().getItem(k).clone();
							clone.setAmount(1);
							if (clone.equals(ammoItem)) {
								j = k;
							}
						}

						if (j == -1) {
							player.sendMessage(Message.GUNS_NOAMMO.raw());
							return;
						}

						reloading.add(player);

						int finalJ = j;
						player.getInventory().getItem(finalJ).setAmount(player.getInventory().getItem(finalJ).getAmount() - 1);

						// TODO: Create animation for the dots
						NotificationUtil.sendNotification(player, Message.GUNS_NOTIFICATIONS_RELOADING.raw(), 3);

						AtomicInteger atomicInteger = new AtomicInteger(0);
						Schedulers.sync().runRepeating((task) -> {
							if (atomicInteger.get() == 30) {
								task.close();
								task.stop();
								reloading.remove(player);
								weapon.setAmmo(model.defaultAmmo());
								weaponManager.updateWeapon(weapon);
								player.sendMessage(Message.GUNS_RELOADING_MESSAGE.raw());
								NotificationUtil.sendNotification(player, Message.GUNS_NOTIFICATIONS_RELOADED.raw(), 1);
								return;
							}

							player.sendTitle(Message.GUNS_RELOADING_TITLE.raw(), Message.GUNS_RELOADING_SUBTITLE.raw(), 0, 1, 15);
							atomicInteger.incrementAndGet();
						}, 0, 1).bindWith(consumer);
						return;
					}

					CooldownMap<String> cooldownMap = cooldowns.get(model);
					if (!cooldownMap.test(license)) {
						return;
					}

					if (model.getClass().isAnnotationPresent(Burst.class)) {
						weapon.setAmmo(weapon.getAmmo() - 3);
						if (weapon.getAmmo() < 0)
							weapon.setAmmo(0);
					} else {
						weapon.setAmmo(weapon.getAmmo() - 1);
					}
					weapon.setDurability(weapon.getDurability() - 1);
					weaponManager.updateWeapon(weapon);

					NotificationUtil.sendNotification(player, Message.GUNS_NOTIFICATIONS_AMMO.format(weapon.getAmmo(),
							model.defaultAmmo()), 1);

					Message.GUNS_INFO.formatAsList(weapon.getDurability(), weapon.getAmmo(), model.defaultAmmo())
							.forEach(e.getPlayer()::sendMessage);

					if (weapon.getDurability() == 0) {
						player.getInventory().setItemInMainHand(null);
						player.sendMessage(Message.GUNS_BROKEN.raw());
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 0.1f, 1f);
						NotificationUtil.sendNotification(player, Message.GUNS_NOTIFICATIONS_BROKEN.raw(), 2);
					}

					if (model.getClass().isAnnotationPresent(Burst.class)) {
						AtomicInteger i = new AtomicInteger();
						Schedulers.sync().runRepeating(task -> {
							if (i.get() == 3) {
								task.stop();
								task.close();
								return;
							}

							Snowball snowball = player.launchProjectile(Snowball.class, player.getLocation().getDirection().multiply(3.7D));

							// Modify data
							snowball.setShooter(player);
							snowball.setCustomName("minetopia_bullet:" + model.modelName());

							i.getAndIncrement();
						}, 0, 2);
					} else if (model.getClass().isAnnotationPresent(Spread.class)) {
						Spread spread = model.getClass().getAnnotation(Spread.class);
						int width = spread.width();

						org.bukkit.util.Vector vector = player.getLocation().getDirection().clone().multiply(1.3d);
						org.bukkit.util.Vector vector2 = player.getLocation().getDirection().clone().multiply(1.3d);

						List<Snowball> snowballs = new ArrayList<>();

						snowballs.add(player.launchProjectile(Snowball.class, vector));

						for (int i = 0; i < (width / 2); i++) {
							vector.add(new Vector(0.0325, 0, 0));
							snowballs.add(player.launchProjectile(Snowball.class, vector));
						}

						for (int i = 0; i < (width / 2); i++) {
							vector2.add(new Vector(0, 0, 0.0325));
							snowballs.add(player.launchProjectile(Snowball.class, vector2));
						}

						snowballs.forEach(object -> {
							object.setShooter(player);
							object.setCustomName("minetopia_bullet:" + model.modelName());
						});
					} else {
						Snowball snowball = player.launchProjectile(Snowball.class, player.getLocation().getDirection().multiply(3.7D));

						// Modify data
						snowball.setShooter(player);
						snowball.setCustomName("minetopia_bullet:" + model.modelName());
					}
				})
				.bindWith(consumer);
	}

}
