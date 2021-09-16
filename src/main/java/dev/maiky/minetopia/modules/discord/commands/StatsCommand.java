package dev.maiky.minetopia.modules.discord.commands;

import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaTime;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import me.lucko.helper.Services;
import me.lucko.helper.profiles.Profile;
import me.lucko.helper.profiles.ProfileRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Door: Maiky
 * Info: Minetopia - 06 Jun 2021
 * Package: dev.maiky.minetopia.modules.discord.events
 */

public class StatsCommand extends ListenerAdapter {

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		if (event.getGuild() == null)
			return;
		if ("stats".equals(event.getName())) {
			String minecraftName = Objects.requireNonNull(event.getOption("ign")).getAsString();
			this.stats(event, minecraftName);
		} else {
			event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
		}
	}

	public void stats(SlashCommandEvent event, String minecraftName) {
		event.deferReply(false).queue();
		InteractionHook hook = event.getHook();

		ProfileRepository repository = Services.load(ProfileRepository.class);
		repository.lookupProfile(minecraftName).thenAcceptAsync(profile -> {
			if (!profile.isPresent()) {
				hook.sendMessageEmbeds(error("Er bestaat geen user met de naam **" + minecraftName + "**!")).queue();
				return;
			}

			Profile dbUser = profile.get();
			OfflinePlayer of = Bukkit.getOfflinePlayer(dbUser.getUniqueId());
			MongoPlayerManager manager = DataModule.getInstance().getPlayerManager();
			MinetopiaUser user = manager.find(u -> u.getUuid().equals(dbUser.getUniqueId()))
					.findFirst().orElse(null);
			if (user == null) {
				hook.sendMessageEmbeds(error("Er bestaat geen user met de naam **" + minecraftName + "**!")).queue();
				return;
			}
			MinetopiaTime time = user.getTime();

			double p = 4000d;
			int left = user.getLevelPoints();
			while(left > p)
				left -= p;
			double percentageProgress = (left / p) * 100;

			long kills = getOfflinePlayerStatistic(of, Statistic.PLAYER_KILLS);
			long deaths = getOfflinePlayerStatistic(of, Statistic.DEATHS);

			String killLabel = "N.v.t",
			deathLabel = "N.v.t",
			kdrLabel = "N.v.t";

			if (kills != -1) {
				killLabel = String.valueOf(kills);
			}
			if (deaths != -1) {
				deathLabel = String.valueOf(deaths);
			}

			if (kills != 0 && deaths != 0) {
				kdrLabel = new DecimalFormat("0.00").format(((double) kills) / (double) deaths);
			}

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle(user.getName());
			builder.setColor(Color.GREEN);
			builder.addField("Level", "Level " + user.getLevel(), true);
			builder.addField("Time Online", String.format("%sd, %su, %sm, %ss", time.getDays(), time.getHours(), time.getMinutes(), time.getSeconds()), true);
			builder.addField("Speelt sinds", new SimpleDateFormat("dd/MM/yyyy").format(new Date(of.getFirstPlayed())), true);
			builder.addField("Level Progress", (int) percentageProgress + "% naar **Level "
					+ ( (int) (Math.floor(user.getLevelPoints() / 4000d) + 1) ) +
					"**", true);
			builder.addField("PvP Statistieken", killLabel + " kills / " + deathLabel + " deaths **(" + kdrLabel + ")**", true);
			builder.setDescription("**Let op:** Deze data is niet altijd accuraat, het kan soms even duren voordat de gegevens die hier staan up to date zijn.");
			builder.setFooter("Developer: Maiky1304", "https://cdn.discordapp.com/icons/822512414461263882/a10014f0cf9bc4078c50a7ea9b5979d7.webp?size=256");
			builder.setThumbnail("http://cravatar.eu/avatar/" + user.getUuid().toString() + "/256.png");

			hook.sendMessageEmbeds(builder.build()).queue();
		});
	}

	public MessageEmbed error(String message) {
		return new EmbedBuilder()
				.setDescription("<a:offline:797832538151845928> " + message)
				.setColor(Color.RED)
				.build();
	}

	public long getOfflinePlayerStatistic(OfflinePlayer player, Statistic statistic) {
		//Default server world always be the first of the list
		//O mundo padrão sempre vai ser o primeiro da lista
		File worldFolder = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "stats");
		File playerStatistics = new File(worldFolder, player.getUniqueId().toString() + ".json");
		if(playerStatistics.exists()){
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(new FileReader(playerStatistics));
			} catch (IOException | ParseException e) {
				Bukkit.getLogger().log(Level.WARNING, "Falha ao ler o arquivo de estatisticas do jogador " + player.getName(), e);
			}
			StringBuilder statisticNmsName = new StringBuilder("stat.");
			for(char character : statistic.name().toCharArray()) {
				if(statisticNmsName.charAt(statisticNmsName.length() - 1) == '_') {
					statisticNmsName.setCharAt(statisticNmsName.length() - 1, Character.toUpperCase(character));
				}else {
					statisticNmsName.append(Character.toLowerCase(character));
				}
			}
			assert jsonObject != null;
			if(jsonObject.containsKey(statisticNmsName.toString())) {
				return (long) jsonObject.get(statisticNmsName.toString());
			}else {
				//This statistic has not yet been saved to file, so it is 0
				//Estatistica ainda não foi salva no arquivo, portato é 0
				return 0;
			}
		}
		//Any statistic can be -1?
		//Alguma estatistica pode virar -1?
		return -1;
	}

}
