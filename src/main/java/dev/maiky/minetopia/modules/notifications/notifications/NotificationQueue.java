package dev.maiky.minetopia.modules.notifications.notifications;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Door: Maiky
 * Info: Minetopia - 10 Jun 2021
 * Package: dev.maiky.minetopia.modules.notifications.notifications
 */

public class NotificationQueue {

	private static final HashMap<UUID, NotificationQueue> queueCache = new HashMap<>();

	public final List<Notification> queue = new ArrayList<>();
	@Getter @Setter
	private Notification current;

	public boolean canNextGo() {
		if (this.queue.size() == 0) return false;
		if (current == null) return true;

		long start = current.getSentAtTime();
		long now = System.currentTimeMillis();
		long maxDiff = (long) (current.getTime() * 1000L);
		long diff = (now - start);

		return diff >= maxDiff;
	}

	public List<Notification> list() {
		return this.queue;
	}

	public static HashMap<UUID, NotificationQueue> getQueueCache() {
		return queueCache;
	}

	public Iterator<Notification> getQueue() {
		return this.queue.iterator();
	}

}
