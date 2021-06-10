package dev.maiky.minetopia.modules.ddgitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.ddgitems.items.classes.ItemData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
                plugin.getLogger().warning("De plugin heeft gefaald om het bestand " + file.toString() + " aan te maken check of de folder wel de juiste permissies heeft.");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
            plugin.getLogger().info("De settings.json kon niet gevonden worden, er is een standaard bestand opgeslagen op " + file.toString());
        }

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(new FileReader(file), JsonObject.class);
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

}