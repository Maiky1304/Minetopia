package dev.maiky.minetopia.modules.players.classes;

import lombok.Getter;
import lombok.Setter;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.players.classes
 */

public class MinetopiaTime {

	@Getter
	@Setter
	private int seconds, minutes, hours, days;

	public MinetopiaTime(int seconds, int minutes, int hours, int days) {
		this.seconds = seconds;
		this.minutes = minutes;
		this.hours = hours;
		this.days = days;
	}

}
