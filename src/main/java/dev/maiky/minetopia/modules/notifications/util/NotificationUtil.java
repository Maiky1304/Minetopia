package dev.maiky.minetopia.modules.notifications.util;

import dev.maiky.minetopia.modules.notifications.notifications.Notification;
import dev.maiky.minetopia.modules.notifications.notifications.NotificationQueue;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.notifications.util
 */

public class NotificationUtil {

	public static void sendNotification(Player player, String message, double timeInSeconds) {
		NotificationQueue queue = NotificationQueue.getQueueCache().get(player.getUniqueId());
		Notification notification = new Notification(player, message, timeInSeconds);
		queue.queue.add(notification);
	}

}
