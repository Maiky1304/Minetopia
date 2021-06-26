package dev.maiky.minetopia.license;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Door: Maiky
 * Info: Minetopia - 01 Jun 2021
 * Package: dev.maiky.licenses
 */

public class Verification {

	private final String key;
	private final Gson gson = new Gson();

	private final JsonObject response;
	private final JavaPlugin plugin;

	private final boolean result;

	public Verification(JavaPlugin plugin, String key) throws IOException {
		this.key = key;
		this.plugin = plugin;

		this.response = this.verify();

		Response res = this.response();
		if (res != Response.VALID) {
			this.result = false;
			if ( res == Response.BANNED ) {
				plugin.getLogger().warning("De plugin is uitgeschakeld omdat het IP adres van de machine waar deze server op draait verbannen is van de license server.");
			} else {
				plugin.getLogger().warning("De plugin is uitgeschakeld omdat de license key incorrect is, pas deze aan in de config!");
			}
			Bukkit.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}

		this.result = true;
		Bukkit.getLogger().info("License key succesvol gevalideerd, bedankt voor het gebruiken maken van een plugin van maiky.dev meer plugins nodig join dan onze discord: https://discord.gg/UP984CFcns");
	}

	public boolean failed() {
		return !this.result;
	}

	private Response response() {
		String content = response.get("content").getAsString();

		if (content.startsWith("you are banned")) return Response.BANNED;

		return Response.valueOf(content);
	}

	private JsonObject verify() throws IOException {
		URL url = new URL("http://54.36.101.109:6590/?license=" + this.key + "&plugin=" + this.plugin.getDescription().getName());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			return gson.fromJson(response.toString(), JsonObject.class);
		}
	}

}