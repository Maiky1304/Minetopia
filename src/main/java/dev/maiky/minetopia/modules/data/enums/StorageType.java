package dev.maiky.minetopia.modules.data.enums;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.data.classes.enums
 */

public enum StorageType {

	MYSQL("helper-sql"),
	MONGODB("helper-mongo");

	private final String associatedPlugin;

	StorageType(String associatedPlugin) {
		this.associatedPlugin = associatedPlugin;
	}

	/**
	 * Get the plugin dependency
	 * @return plugin name
	 */
	public String getAssociatedPlugin() {
		return associatedPlugin;
	}
}
