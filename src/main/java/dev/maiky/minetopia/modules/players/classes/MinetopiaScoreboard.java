package dev.maiky.minetopia.modules.players.classes;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.districts.DistrictsModule;
import dev.maiky.minetopia.modules.levels.manager.LevelCheck;
import dev.maiky.minetopia.modules.players.PlayersModule;
import dev.maiky.minetopia.util.Numbers;
import dev.maiky.minetopia.util.Text;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 22 May 2021
 * Package: dev.maiky.minetopia.modules.players.classes
 */

public class MinetopiaScoreboard {

	@Getter
	private final BPlayerBoard playerBoard;

	private final HashMap<Integer, String> staticLines = new HashMap<>(),
	updateLines = new HashMap<>(), all = new HashMap<>();

	public MinetopiaScoreboard(Player player) {
		Netherboard netherboard = Netherboard.instance();
		this.playerBoard = netherboard.createBoard(player, Text.colors("&l" + PlayersModule.getInstance().getCityName().toUpperCase()));
	}

	public void initialize() {
		List<String> lines = PlayersModule.getInstance().getLayout();
		int index = 0;
		for (int i = lines.size(); i != 0; i--) {
			String line = lines.get(index);
			if (line.startsWith("%u%"))
				updateLines.put(i, line);
			else staticLines.put(i, line);
			index++;
		}

		all.putAll(updateLines);
		all.putAll(staticLines);

		HashMap<Integer, String> processed = placeholders(this.playerBoard.getPlayer(), all);
		for (Integer i : processed.keySet()) {
			String value = processed.get(i);
			this.playerBoard.set(value, i);
		}
	}

	public void update() {
		HashMap<Integer, String> processed = placeholders(this.playerBoard.getPlayer(), updateLines);
		for (Integer i : processed.keySet()) {
			String value = processed.get(i);
			this.playerBoard.set(value, i);
		}
	}

	public HashMap<Integer, String> placeholders(Player player, HashMap<Integer, String> list) {
		MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());
		DecimalFormat shardsFormat = new DecimalFormat("0.0");
		Economy economy = Minetopia.getEconomy();
		LevelCheck check = new LevelCheck(user);

		HashMap<String, Object> replacements = new HashMap<>();
		replacements.put("%area%", DistrictsModule.getLocationCache().get(player.getUniqueId()) == null ?
				PlayersModule.getInstance().getCityName() : DistrictsModule.getLocationCache().get(player.getUniqueId()));
		replacements.put("%temperature%", "0.0°C");
		replacements.put("%level%", user.getLevel());
		replacements.put("%possiblelevel%", check.calculatePossibleLevel());
		replacements.put("%difference%", check.createLevelString());
		replacements.put("%grayshards%", shardsFormat.format(user.getGrayshards()));
		replacements.put("%goldshards%", shardsFormat.format(user.getGoldshards()));
		replacements.put("%balance%", Numbers.convert(Numbers.Type.MONEY, economy.getBalance(player)));
		replacements.put("%c%", String.format("§%s", DistrictsModule.getLocationCache().get(player.getUniqueId()) == null ?
				PlayersModule.getInstance().getCityColor() : DistrictsModule.getBlockCache().get(DistrictsModule.getLocationCache().get(player.getUniqueId())) == null ? PlayersModule.getInstance().getCityColor() : DistrictsModule.getBlockCache().get(DistrictsModule.getLocationCache().get(player.getUniqueId()))));
		replacements.put("%u%", "");

		int empty = 0;
		HashMap<Integer, String> returnList = new HashMap<>();
		for (Integer i : list.keySet()) {
			String s = list.get(i);
			for (String replacer : replacements.keySet()) {
				if (s.equals("%none%")) {
					returnList.put(i, String.format("§%s", empty));
					empty++;
					break;
				}

				if (s.contains(replacer)) {
					s = s.replaceAll(replacer, String.valueOf(replacements.get(replacer)));
				}
				returnList.put(i, Text.colors(s));
			}
		}

		return returnList;
	}

}
