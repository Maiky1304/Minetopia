package dev.maiky.minetopia.modules.players.classes;

import dev.maiky.minetopia.modules.colors.packs.ChatColor;
import dev.maiky.minetopia.modules.colors.packs.LevelColor;
import dev.maiky.minetopia.modules.players.PlayersModule;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.players.classes
 */

public class MinetopiaUser {

	@Getter
	private final UUID uuid;
	@Getter
	private final String name;
	@Getter @Setter
	private int level;
	@Getter
	private final MinetopiaTime time;
	@Getter @Setter
	private double grayshards, goldshards;
	@Getter @Setter
	private String cityColor;
	@Getter @Setter
	private MinetopiaUpgrades minetopiaUpgrades;
	@Getter @Setter
	private int levelPoints;
	@Getter @Setter
	private boolean policeChat;
	@Getter @Setter
	private String currentPrefix;
	@Getter
	private final List<String> prefixes = new ArrayList<>();
	@Getter @Setter
	private ChatColor currentChatColor;
	@Getter
	private final HashMap<ChatColor, String> chatColors = new HashMap<>();
	@Getter @Setter
	private LevelColor currentLevelColor;
	@Getter
	private final HashMap<LevelColor, String> levelColors = new HashMap<>();
	@Getter @Setter
	private ChatColor currentPrefixColor;
	@Getter
	private final HashMap<ChatColor, String> prefixColors = new HashMap<>();
	@Getter
	private boolean scoreboard, actionbar;
	@Getter @Setter
	private int grayshardBoost;
	@Getter @Setter
	private int goldshardBoost;

	public MinetopiaUser(UUID uuid) {
		this.uuid = uuid;

		final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		if (!offlinePlayer.isOnline()) {
			this.name = "???";
		} else {
			this.name = offlinePlayer.getName();
		}

		this.level = PlayersModule.getInstance().getLevel();
		this.time = new MinetopiaTime(0,0,0,0);
		this.grayshards = PlayersModule.getInstance().getShards();
		this.cityColor = PlayersModule.getInstance().getCity();
		this.minetopiaUpgrades = new MinetopiaUpgrades();
		this.currentChatColor = ChatColor.CHATCOLOR_NORMAL_GRAY;
		this.currentLevelColor = LevelColor.CHATCOLOR_NORMAL_AQUA;
		this.currentPrefixColor = ChatColor.CHATCOLOR_NORMAL_GRAY;
		this.currentPrefix = "Burger";
		this.getPrefixes().add(this.currentPrefix);
	}

	public MinetopiaUser(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.level = PlayersModule.getInstance().getLevel();
		this.time = new MinetopiaTime(0,0,0,0);
		this.grayshards = PlayersModule.getInstance().getShards();
		this.cityColor = PlayersModule.getInstance().getCity();
		this.minetopiaUpgrades = new MinetopiaUpgrades();
		this.currentChatColor = ChatColor.CHATCOLOR_NORMAL_GRAY;
		this.currentLevelColor = LevelColor.CHATCOLOR_NORMAL_AQUA;
		this.currentPrefixColor = ChatColor.CHATCOLOR_NORMAL_GRAY;
		this.currentPrefix = "Burger";
		this.getPrefixes().add(this.currentPrefix);
	}
}
