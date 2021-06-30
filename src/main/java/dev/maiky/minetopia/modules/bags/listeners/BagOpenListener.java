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

package dev.maiky.minetopia.modules.bags.listeners;

import dev.maiky.minetopia.modules.bags.bag.Bag;
import dev.maiky.minetopia.modules.bags.bag.BagType;
import dev.maiky.minetopia.modules.bags.ui.KofferUI;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.BagManager;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.security.commands.BodySearchCommand;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.SerializationUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BagOpenListener implements TerminableModule {

	final List<Material> materialList = new ArrayList<>();

	public BagOpenListener() {
		for (BagType value : BagType.values()) materialList.add(value.material);
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerInteractEvent.class)
				.filter(e -> !BodySearchCommand.getBeingSearched().containsKey(e.getPlayer().getUniqueId()))
				.filter(PlayerInteractEvent::hasItem)
				.filter(e -> e.getAction().toString().startsWith("RIGHT_CLICK"))
				.filter(e -> materialList.contains(e.getItem().getType()))
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> {
					ItemStack nms = CraftItemStack.asNMSCopy(e.getItem());
					if (nms.getTag() == null)
						return false;
					return nms.getTag().hasKey("id");
				}).handler(e ->
		{
			e.setCancelled(true);

			ItemStack nms = CraftItemStack.asNMSCopy(e.getItem());
			NBTTagCompound tagCompound = nms.getTag();

			assert tagCompound != null;
			int id = tagCompound.getInt("id");

			BagManager bagManager = BagManager.with(DataModule.getInstance().getSqlHelper());
			Bag bag = bagManager.getBag(id);
			if (bag == null) {
				e.getPlayer().sendMessage(Message.BAGS_ERROR_OPEN_SELF.raw());
				return;
			}

			bag.getHistory().put(e.getPlayer().getName(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
			bagManager.saveBag(bag);

			org.bukkit.inventory.ItemStack[] itemStacks = SerializationUtils.itemStackArrayFromBase64(bag.getBase64Contents());
			KofferUI kofferUI = new KofferUI(e.getPlayer(), bag.getId(), itemStacks, 0);
			kofferUI.open();

			e.getPlayer().sendMessage(Message.BAGS_OPEN.raw());
		}).bindWith(consumer);
	}

}
