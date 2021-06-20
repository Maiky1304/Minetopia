package dev.maiky.minetopia.modules.ddgitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.ddgitems.items.classes.ItemData;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ItemLoader {

    public static List<ItemData> items = new ArrayList<>();

    public void load() throws FileNotFoundException {
        final Minetopia plugin = Minetopia.getPlugin(Minetopia.class);

        File folder = new File(plugin.getDataFolder().toString());
        if (!folder.exists()){
            folder.mkdir();
        }

        File file = new File(folder.toString() + "/resourcepack/ddgitems.json");
        if (!file.exists()){
            try {
                plugin.saveResource("resourcepack/ddgitems.json", false);
            } catch (Exception e) {
                plugin.getLogger().warning("     §4§l✗§r De plugin heeft gefaald om het bestand " + file.toString() + " aan te maken check of de folder wel de juiste permissies heeft.");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
            plugin.getLogger().info("     §4§l✗§r De settings.json kon niet gevonden worden, er is een standaard bestand opgeslagen op " + file.toString());
        }

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(new FileReader(file), JsonObject.class);

        if (plugin.getConfiguration().get().getBoolean("settings.ddgitems.auto-update")) {
            plugin.getLogger().info("     §e§l*§r Checking for updates with DDGItems...");
            String version = jsonObject.get("version").getAsString();
            String latest = getLatest();
            if ( !version.equals(latest) ) {
                plugin.getLogger().info("     §e§l*§r Found newer version for DDGItems! (v" + latest + ") (Current: v" + version + ")");
                plugin.getLogger().info("     §e§l*§r Automatically updating to latest version...");
                updateTo(latest, file);
                jsonObject = gson.fromJson(new FileReader(file), JsonObject.class);
            } else {
                plugin.getLogger().info("     §a§l✓§r You are already on the latest version of DDGItems you're good to go!");
            }
        }

        JsonArray itemArray = jsonObject.getAsJsonArray("items");

        for (JsonElement element : itemArray) {
            if (!(element instanceof JsonObject))
                continue;
            try {
                items.add(new ItemData(element.getAsJsonObject()));
            } catch (Exception ignored) {
            }
        }
    }

    private void updateTo(String version, File file) {
        Minetopia plugin = Minetopia.getPlugin(Minetopia.class);
        String unreplacedUrl = "https://ddgitems.minetopiasdb.nl/versions/%s.json";
        String urlRaw = String.format(unreplacedUrl, version);

        try {
            URL sdb = new URL(urlRaw);
            InputStream stream = sdb.openStream();
            Files.copy(stream, Paths.get(file.toString()), StandardCopyOption.REPLACE_EXISTING);
            stream.close();

            plugin.getLogger().info("     §a§l✓§r Succesfully updated DDGItems to v" + version);
        } catch (Exception exception) {
            Bukkit.getLogger().warning("     §4§l✗§r Error ocurred whilst trying to update DDGItems to v" + version);
            Bukkit.getLogger().warning("     §4§l✗§r Please contact Maiky#0001 on Discord as soon as possible to figure out a fix.");
        }
    }

    private String getLatest() {
        Minetopia plugin = Minetopia.getPlugin(Minetopia.class);
        StringBuilder content = new StringBuilder();

        try {
            URL sdb = new URL("https://ddgitems.minetopiasdb.nl/versions.json");
            BufferedReader in = new BufferedReader(new InputStreamReader(sdb.openStream()));

            String data;
            while((data = in.readLine()) != null)
                content.append(data);
            in.close();
        } catch (Exception exception) {
            Bukkit.getLogger().warning("     §4§l✗§r Error ocurred trying to retrieve the latest version of DDGItems.");
            Bukkit.getLogger().warning("     §4§l✗§r Please contact Maiky#0001 on Discord as soon as possible to figure out a fix.");
            return "error";
        }

        Gson gson = new Gson();
        JsonObject object = gson.fromJson(content.toString(), JsonObject.class);
        return object.get("latest").getAsString();
    }

}