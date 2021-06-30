package dev.maiky.minetopia.modules.boosters.tasks;

import dev.maiky.minetopia.modules.boosters.enums.BoosterType;
import dev.maiky.minetopia.modules.boosters.manager.SystemBoosterManager;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.util.Message;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.NotNull;

/**
 * Door: Maiky
 * Info: Minetopia - 17 Jun 2021
 * Package: dev.maiky.minetopia.modules.boosters.booster.tasks
 */

public class BoostTask implements TerminableModule {

	@Getter
	private static int totalGrayShard = 0;
	@Getter
	private static int totalGoldShard = 0;

	@Getter
	private static BossBar grayshards = null;
	@Getter
	private static BossBar goldshards = null;

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		SystemBoosterManager systemBoosterManager = SystemBoosterManager.with(DataModule.getInstance().getSqlHelper());

		Schedulers.async().runRepeating(task -> {
			int totalGrayshards = systemBoosterManager.get(BoosterType.GRAYSHARD);
			int totalGoldshards = systemBoosterManager.get(BoosterType.GOLDSHARD);

			totalGrayShard = totalGoldshards;
			totalGoldShard = totalGoldshards;

			String lastGrayshards = systemBoosterManager.getLastUser(BoosterType.GRAYSHARD);
			String lastGoldshards = systemBoosterManager.getLastUser(BoosterType.GOLDSHARD);

			long expiryGrayshards = systemBoosterManager.getExpiry(BoosterType.GRAYSHARD);
			double progressGrayshards = ((double)(expiryGrayshards - System.currentTimeMillis()) / 3600000L);

			if (System.currentTimeMillis() > expiryGrayshards) {
				systemBoosterManager.update("-", BoosterType.GRAYSHARD, 0);
			}

			long expiryGoldshards = systemBoosterManager.getExpiry(BoosterType.GOLDSHARD);
			double progressGoldshards = ((double)(expiryGoldshards - System.currentTimeMillis()) / 3600000L);

			if (System.currentTimeMillis() > expiryGoldshards) {
				systemBoosterManager.update("-", BoosterType.GOLDSHARD, 0);
			}

			if (totalGrayshards != 0) {
				String title = Message.BOOSTERS_BOSSBAR_GRAYSHARD_TITLE.format(totalGrayshards, lastGrayshards);
				if (grayshards == null) {
					grayshards = Bukkit.createBossBar(title, BarColor.valueOf(Message.BOOSTERS_BOSSBAR_GRAYSHARD_BARCOLOR.raw()),
							BarStyle.valueOf(Message.BOOSTERS_BOSSBAR_GRAYSHARD_BARSTYLE.raw()));
				} else {
					grayshards.setTitle(title);
				}
				grayshards.setProgress(progressGrayshards);
				grayshards.setVisible(true);
				grayshards.removeAll();
				Bukkit.getOnlinePlayers().forEach(grayshards::addPlayer);
			} else if (grayshards != null) {
				grayshards.removeAll();
				grayshards = null;
			}

			if (totalGoldshards != 0) {
				String title = Message.BOOSTERS_BOSSBAR_GOLDSHARD_TITLE.format(totalGoldshards, lastGoldshards);
				if (goldshards == null) {
					goldshards = Bukkit.createBossBar(title, BarColor.valueOf(Message.BOOSTERS_BOSSBAR_GOLDSHARD_BARCOLOR.raw()),
							BarStyle.valueOf(Message.BOOSTERS_BOSSBAR_GOLDSHARD_BARSTYLE.raw()));
				} else {
					goldshards.setTitle(title);
				}
				goldshards.setProgress(progressGoldshards);
				goldshards.setVisible(true);
				goldshards.removeAll();
				Bukkit.getOnlinePlayers().forEach(goldshards::addPlayer);
			} else if (goldshards != null) {
				goldshards.removeAll();
				goldshards = null;
			}
		}, 0, 20).bindWith(consumer);
	}

}
