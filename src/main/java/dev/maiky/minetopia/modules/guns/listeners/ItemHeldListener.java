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
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ItemHeldListener implements TerminableModule {

	private final WeaponManager weaponManager;

	public ItemHeldListener(WeaponManager weaponManager) {
		this.weaponManager = weaponManager;
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerItemHeldEvent.class)
				.filter(e -> {
					System.out.println(e.getPlayer().getInventory().getItem(e.getNewSlot()) != null);
					return e.getPlayer().getInventory().getItem(e.getNewSlot()) != null;
				})
				.filter(e -> {
					System.out.println(e.getPlayer().getInventory().getItem(e.getNewSlot()).getType() == Material.WOOD_HOE);
					return e.getPlayer().getInventory().getItem(e.getNewSlot()).getType() == Material.WOOD_HOE;
				})
				.filter(e -> {
					System.out.println(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItem(e.getNewSlot())).getTag() != null);
					return CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItem(e.getNewSlot())).getTag() != null;
				})
				.filter(e -> {
					System.out.println(Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItem(e.getNewSlot())).getTag())
							.hasKey("mtcustom"));
					return Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItem(e.getNewSlot())).getTag())
							.hasKey("mtcustom");
				})
				.handler(e -> {
					NBTTagCompound nbtTagCompound = Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItem(e.getNewSlot())).getTag());
					Model model = GunsModule.getInstance().getModel(nbtTagCompound.getString("mtcustom"));
					String license = nbtTagCompound.getString("license");
					Weapon weapon = weaponManager.getWeaponByLicense(license);

					if (weapon == null) {
						e.getPlayer().sendMessage("§cHet wapen wat je probeerde vast te houden bestaat niet meer!");
						e.getPlayer().getInventory().setItem(e.getNewSlot(), null);
						e.getPlayer().updateInventory();
						return;
					}

					List<String> gunsInfo = Message.GUNS_INFO.formatAsList(weapon.getDurability(), weapon.getAmmo(), model.defaultAmmo());
					gunsInfo.forEach(e.getPlayer()::sendMessage);
				}).bindWith(consumer);
	}

}
