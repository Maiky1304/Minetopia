package dev.maiky.minetopia.modules.transportation.portal;

import lombok.Getter;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.transportation.portal
 */

public class LocalPortalData {

	@Getter
	private final ILocation location;
	@Getter
	private final String server;

	public LocalPortalData(ILocation location, String server) {
		this.location = location;
		this.server = server;
	}

}
