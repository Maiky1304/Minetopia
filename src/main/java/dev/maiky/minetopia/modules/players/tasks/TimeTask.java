package dev.maiky.minetopia.modules.players.tasks;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.boosters.tasks.BoostTask;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaTime;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Numbers;
import dev.maiky.minetopia.util.Options;
import dev.maiky.minetopia.util.Text;
import me.lucko.helper.bucket.Bucket;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 22 May 2021
 * Package: dev.maiky.minetopia.modules.players.tasks
 */

public class TimeTask implements Runnable {

	private final Bucket<Player> bucket;

	public TimeTask(Bucket<Player> bucket) {
		this.bucket = bucket;
	}

	@Override
	public void run() {
		for (Player player : bucket) {
			MinetopiaUser user = MongoPlayerManager.getCache().get(player.getUniqueId());
			if (user == null) continue;
			MinetopiaTime time = user.getTime();

			boolean shards = false;
			boolean loan = false;

			int seconds = time.getSeconds(), minutes = time.getMinutes(), hours = time.getHours(),
					days = time.getDays();

			seconds++;

			if ( seconds >= 60 ) {
				seconds = 0;
				minutes++;

				if ( minutes == 10 || minutes == 20 || minutes == 30 || minutes == 40 || minutes == 50 || minutes == 60 ) {
					shards = true;
				}
			}
			if ( minutes >= 60 ) {
				minutes = 0;
				hours++;
				loan = true;
			}
			if ( hours >= 24 ) {
				hours = 0;
				days++;

				if (Options.TIME_DAILYUPGRADETOKEN.asBoolean().get()) {
					user.getMinetopiaUpgrades().setPoints(user.getMinetopiaUpgrades().getPoints() + 1);
					player.sendMessage("§6Je hebt §c1 §6upgrade token ontvangen omdat je §c1 dag§6 playtime erbij hebt!");
				}
			}

			time.setSeconds(seconds);
			time.setMinutes(minutes);
			time.setHours(hours);
			time.setDays(days);

			if ( loan && Options.TIME_LOANENABLED.asBoolean().get() ) {
				int level = user.getLevel();
				double payout = level * Options.TIME_LOANINCREASEPERLVL.asDouble().get();
				Economy economy = Minetopia.getEconomy();
				EconomyResponse response = economy.depositPlayer(player, payout);
				if ( response.transactionSuccess() ) {
					String message = "&6Gefeliciteerd, je hebt zojuist je uurloon van &c%s &6ontvangen.";
					player.sendMessage(Text.colors(String.format(message, Numbers.convert(Numbers.Type.MONEY, payout))));
				} else {
					player.sendMessage("§cError: Er is iets fout gegaan bij het verwerken van de uurloon transactie, contacteer iemand van Lead voor hulp.");
				}
			}

			if ( shards && Options.TIME_SHARDPAYOUTENABLED.asBoolean().get() ) {
				double grayOut = Options.TIME_GRAYSHARDS_PER10MIN.asDouble().get();
				double goldOut = Options.TIME_GOLDSHARDS_PER10MIN.asDouble().get();

				int boostGray = BoostTask.getTotalGrayShard();
				int boostGold = BoostTask.getTotalGoldShard();
				grayOut = grayOut / 100 * (100 + boostGray);
				goldOut = goldOut / 100 * (100 + boostGold);

				user.setGrayshards(user.getGrayshards() + grayOut);
				user.setGoldshards(user.getGoldshards() + goldOut);

				String message = "&6Gefeliciteerd, je hebt zojuist &c%s &6%s ontvangen!";
				player.sendMessage(Text.colors(String.format(message, Numbers.convert(Numbers.Type.SHARDS, grayOut), "Grayshard")));
				player.sendMessage(Text.colors(String.format(message, Numbers.convert(Numbers.Type.SHARDS, goldOut), "GoldShard")));
			}
		}
	}

}
