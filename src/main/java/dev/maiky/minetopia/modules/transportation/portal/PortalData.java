package dev.maiky.minetopia.modules.transportation.portal;

import lombok.Getter;
import org.bukkit.Location;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.transportation.portal
 */

public class PortalData {

	@Getter
	private final String location;
	@Getter
	private final String server;

	public PortalData(Location location, String server) {
		this.location = location.toString();
		this.server = server;
	}
}
