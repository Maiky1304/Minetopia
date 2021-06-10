package dev.maiky.minetopia.modules.players.tasks;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaTime;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Numbers;
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
			MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());
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
			}

			time.setSeconds(seconds);
			time.setMinutes(minutes);
			time.setHours(hours);
			time.setDays(days);

			if ( loan ) {
				int level = user.getLevel();
				double payout = level * 2500;
				Economy economy = Minetopia.getEconomy();
				EconomyResponse response = economy.depositPlayer(player, payout);
				if ( response.transactionSuccess() ) {
					String message = "&6Gefeliciteerd, je hebt zojuist je uurloon van &c%s &6ontvangen.";
					player.sendMessage(Text.colors(String.format(message, Numbers.convert(Numbers.Type.MONEY, payout))));
				} else {
					player.sendMessage("§cError: Er is iets fout gegaan bij het verwerken van de uurloon transactie, contacteer iemand van Lead voor hulp.");
				}
			}

			if ( shards ) {
				double payout = 0.7;
				user.setShards(user.getShards() + payout);
				String message = "&6Gefeliciteerd, je hebt zojuist &c%s &6BlackShard ontvangen!";
				player.sendMessage(Text.colors(String.format(message, Numbers.convert(Numbers.Type.SHARDS, payout))));
			}
		}
	}

}
