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

package dev.maiky.minetopia.modules.security.listeners;

import dev.maiky.minetopia.modules.security.SecurityModule;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DetectorListener implements TerminableModule {

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerInteractEvent.class)
				.filter(e -> e.getAction() == Action.PHYSICAL)
				.filter(e -> e.getClickedBlock().getType().toString().endsWith("PLATE"))
				.filter(e -> e.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN)
						.getType().toString().contains("SIGN"))
				.handler(e -> {
					Player p = e.getPlayer();

					Sign sign = (Sign) e.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getState();
					if (!sign.getLine(0).equals("[DETECTOR]")) {
						return;
					}
					int radius = Integer.parseInt(sign.getLine(1));

					boolean carryingBag = false;
					boolean carryingIllegalItems = false;
					for (Material material : SecurityModule.getIllegalItems()) {
						if (p.getInventory().getItemInOffHand() != null) {
							if (p.getInventory().getItemInOffHand().getType() == material) {
								carryingIllegalItems = true;
								break;
							}
						}

						if (p.getInventory().contains(material)) {
							carryingIllegalItems = true;
							break;
						}
					}

					if (!carryingIllegalItems){

						if (p.getInventory().contains(Material.CARROT_STICK)) {
							carryingBag = true;
						}

					}

					List<Player> nearbyPlayers = new ArrayList<>();
					for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if (onlinePlayer.getLocation().distance(p.getLocation())
								<= 25) nearbyPlayers.add(onlinePlayer);
					}

					List<Block> blocks = SecurityModule.getDetectionBlocks(e.getClickedBlock().getLocation(), radius);
					if (blocks.isEmpty())return;

					for(Block block : blocks) {
						for (Player nearbyPlayer : nearbyPlayers) {
							nearbyPlayer.sendBlockChange(block.getLocation(), Material.WOOL, (carryingIllegalItems ? (byte)14 : (carryingBag ? (byte)4 : 13)));
							Schedulers.sync().runLater(() -> {
								nearbyPlayer.sendBlockChange(block.getLocation(), Material.WOOL, (byte)15);
							}, 40).bindWith(consumer);
						}
					}
				}).bindWith(consumer);
	}
}
