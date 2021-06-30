package dev.maiky.minetopia.modules.players.classes;

import dev.maiky.minetopia.modules.colors.packs.ChatColor;
import dev.maiky.minetopia.modules.colors.packs.LevelColor;
import dev.maiky.minetopia.modules.players.PlayersModule;
import dev.maiky.minetopia.modules.transportation.portal.LocalPortalData;
import dev.maiky.minetopia.modules.transportation.portal.PortalData;
import dev.maiky.minetopia.util.Options;
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
	private @Getter @Setter LocalPortalData portalData;
	private @Getter final MinetopiaData minetopiaData;

	public MinetopiaUser(UUID uuid) {
		this.uuid = uuid;

		final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		if (!offlinePlayer.isOnline()) {
			this.name = "???";
		} else {
			this.name = offlinePlayer.getName();
		}

		this.level = Options.PLAYER_DEFAULT_LEVEL.asInt().get();
		this.time = new MinetopiaTime(0,0,0,0);
		this.grayshards = Options.PLAYER_DEFAULT_GRAYSHARDS.asDouble().get();
		this.goldshards = Options.PLAYER_DEFAULT_GOLDSHARDS.asDouble().get();
		this.cityColor = Options.PLAYER_DEFAULT_CITYCOLOR.asString().get();
		this.minetopiaUpgrades = new MinetopiaUpgrades();
		this.currentChatColor = ChatColor.CHATCOLOR_NORMAL_GRAY;
		this.currentLevelColor = LevelColor.CHATCOLOR_NORMAL_AQUA;
		this.currentPrefixColor = ChatColor.CHATCOLOR_NORMAL_GRAY;
		this.currentPrefix = Options.PLAYER_DEFAULT_PREFIX.asString().get();
		this.getPrefixes().add(this.currentPrefix);
		this.scoreboard = true;
		this.actionbar = true;
		this.minetopiaData = new MinetopiaData(offlinePlayer.isOnline() ? MinetopiaInventory.of(offlinePlayer.getPlayer().getInventory()) : MinetopiaInventory.empty(),
				20, 20, uuid);
	}

	public MinetopiaUser(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.level = Options.PLAYER_DEFAULT_LEVEL.asInt().get();
		this.time = new MinetopiaTime(0,0,0,0);
		this.grayshards = Options.PLAYER_DEFAULT_GRAYSHARDS.asDouble().get();
		this.goldshards = Options.PLAYER_DEFAULT_GOLDSHARDS.asDouble().get();
		this.cityColor = Options.PLAYER_DEFAULT_CITYCOLOR.asString().get();
		this.minetopiaUpgrades = new MinetopiaUpgrades();
		this.currentChatColor = ChatColor.CHATCOLOR_NORMAL_GRAY;
		this.currentLevelColor = LevelColor.CHATCOLOR_NORMAL_AQUA;
		this.currentPrefixColor = ChatColor.CHATCOLOR_NORMAL_GRAY;
		this.currentPrefix = Options.PLAYER_DEFAULT_PREFIX.asString().get();
		this.getPrefixes().add(this.currentPrefix);
		this.scoreboard = true;
		this.actionbar = true;
		final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		this.minetopiaData = new MinetopiaData(offlinePlayer.isOnline() ? MinetopiaInventory.of(offlinePlayer.getPlayer().getInventory()) : MinetopiaInventory.empty(),
				20, 20, uuid);
	}
}
