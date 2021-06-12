package dev.maiky.minetopia.modules.notifications;

import co.aikar.commands.BukkitCommandManager;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.notifications.command.NotificationCommand;
import dev.maiky.minetopia.modules.notifications.notifications.Notification;
import dev.maiky.minetopia.modules.notifications.notifications.NotificationQueue;
import dev.maiky.minetopia.util.Numbers;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.BucketPartition;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * Door: Maiky
 * Info: Minetopia - 10 Jun 2021
 * Package: dev.maiky.minetopia.modules.notifications
 */

public class NotificationsModule implements MinetopiaModule {

	private boolean enabled;
	private final CompositeTerminable terminable = CompositeTerminable.create();

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.disable();
		this.enable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		// Register Events
		this.registerEvents();

		// Register Tasks
		this.registerTasks();

		// Register Commands
		this.registerCommands();
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager manager = minetopia.getCommandManager();

		manager.registerCommand(new NotificationCommand());
	}

	private void registerTasks() {
		Bucket<Player> bucket = BucketFactory.newHashSetBucket(10, PartitioningStrategies.lowestSize());
		if ( Bukkit.getOnlinePlayers().size() != 0 )
			bucket.addAll(Bukkit.getOnlinePlayers());

		Events.subscribe(PlayerJoinEvent.class).handler(e -> bucket.add(e.getPlayer())).bindWith(terminable);
		Events.subscribe(PlayerQuitEvent.class).handler(e -> bucket.remove(e.getPlayer())).bindWith(terminable);

		Schedulers.sync().runRepeating(task -> {
			BucketPartition<Player> part = bucket.asCycle().next();
			for (Player player : Bukkit.getOnlinePlayers().size() >= 50 ? part : Bukkit.getOnlinePlayers()) {
				NotificationQueue queue = NotificationQueue.getQueueCache().get(player.getUniqueId());
				if (queue.canNextGo()) {
					Iterator<Notification> iterator = queue.getQueue();
					if (!iterator.hasNext()) return;
					Notification notification = iterator.next();
					notification.setSentAtTime(System.currentTimeMillis());
					notification.setFinished(false);
					queue.setCurrent(notification);
					queue.queue.remove(notification);
					CompositeTerminable compositeTerminable = CompositeTerminable.create();
					notification.setup(compositeTerminable);
				}
			}
		}, 0, 1).bindWith(terminable);
	}

	private void registerEvents() {
		Events.subscribe(PlayerJoinEvent.class).handler(e -> NotificationQueue.getQueueCache().put(e.getPlayer().getUniqueId(), new NotificationQueue()))
		.bindWith(terminable);
		Events.subscribe(PlayerQuitEvent.class).handler(e -> NotificationQueue.getQueueCache().remove(e.getPlayer().getUniqueId()))
		.bindWith(terminable);
		Events.subscribe(UserBalanceUpdateEvent.class)
				.handler(e -> {
					String amount = Numbers.convert(Numbers.Type.MONEY, e.getNewBalance().doubleValue() - e.getOldBalance().doubleValue());
					ChatColor color = amount.contains("-") ? ChatColor.RED : ChatColor.GREEN;
					NotificationQueue queue = NotificationQueue.getQueueCache().get(e.getPlayer().getUniqueId());
					Notification notification = new Notification(e.getPlayer(), color + (amount.contains("-") ? "-" : "+") +
							Numbers.convert(Numbers.Type.MONEY, Math.abs(e.getNewBalance().doubleValue() - e.getOldBalance().doubleValue())), 2);
					queue.queue.add(notification);
				}).bindWith(terminable);
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			this.terminable.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Notifications";
	}

}
