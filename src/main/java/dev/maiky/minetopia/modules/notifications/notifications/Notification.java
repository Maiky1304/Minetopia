package dev.maiky.minetopia.modules.notifications.notifications;

import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Door: Maiky
 * Info: Minetopia - 10 Jun 2021
 * Package: dev.maiky.minetopia.modules.notifications
 */

public class Notification implements TerminableModule {

	@Getter
	private final Player recipent;
	@Getter
	private final String message;
	@Getter
	private final double time;
	@Getter @Setter
	private long sentAtTime;
	@Getter @Setter
	private boolean finished = false;

	public Notification(Player recipent, String message, double time) {
		this.recipent = recipent;
		this.message = message;
		this.time = time;
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Schedulers.sync().runRepeating((task) ->
				{
					long now = System.currentTimeMillis();
					long expiry = sentAtTime + (long) (this.time * 1000L);

					if (now > expiry) {
						task.stop();
						task.close();
						NotificationQueue.getQueueCache().get(recipent.getUniqueId()).setCurrent(null);
						return;
					}

					recipent.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
				},
				0, 1).bindWith(consumer);
	}

}
