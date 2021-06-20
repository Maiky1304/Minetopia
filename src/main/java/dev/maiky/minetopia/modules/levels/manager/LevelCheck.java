package dev.maiky.minetopia.modules.levels.manager;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.levels.enums.LevelState;
import dev.maiky.minetopia.modules.levels.plots.IPlot;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.levels.manager
 */

public class LevelCheck {

	private final MinetopiaUser user;

	public LevelCheck(MinetopiaUser user) {
		this.user = user;
	}

	private int justCalculated = -1;

	public int calculatePoints() {
		if (justCalculated != -1)
			return justCalculated;

		int requiredPoints = 4000;

		int perPlot = requiredPoints;
		int perShard = 50;
		int per50k = 1000;
		int perDayTime = requiredPoints;
		int perVehicle = 1000;

		int total = 0;

		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		PlotManager plotManager = PlotManager.with(minetopia.dataModule.getSqlHelper());
		List<IPlot> plots = new ArrayList<>(); // plotManager.getOwnedPlots(this.user.getUuid())

		total += perPlot * plots.size();
		total += perShard * Math.round(user.getGrayshards());
		total += per50k * Math.round( Minetopia.getEconomy().getBalance(Bukkit.getOfflinePlayer(this.user.getUuid())) / 50000 );
		total += perDayTime * user.getTime().getDays();
		//total += perVehicle * 0 TODO: to be added;

		this.justCalculated = total + requiredPoints;
		return Math.min(this.justCalculated, 1000000);
	}

	public int calculatePossibleLevel() {
		return user.getLevelPoints() / 4000;
	}

	public String createLevelString() {
		int possible = this.calculatePossibleLevel();
		LevelState state = canChange();

		if (state == LevelState.POSITIVE)
			return "&a+" + (possible - user.getLevel()) + "&r";
		else if (state == LevelState.NEGATIVE)
			return "&c" + (possible - user.getLevel()) + "&r";
		else return "&6" + (possible - user.getLevel()) + "&r";
	}

	public LevelState canChange() {
		int possible = calculatePossibleLevel();
		if (possible == user.getLevel())
			return LevelState.NO_CHANGE;
		if (possible < user.getLevel())
			return LevelState.NEGATIVE;
		return LevelState.POSITIVE;
	}

}
