package dev.maiky.minetopia.modules.discord.listeners;

import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Door: Maiky
 * Info: Minetopia - 06 Jun 2021
 * Package: dev.maiky.minetopia.modules.discord.listeners
 */

public class MinecraftCommandLogger implements TerminableModule {

	private final JDA jda;
	private final long commandLogs;

	public MinecraftCommandLogger(JDA jda, long commandLogs) {
		this.jda = jda;
		this.commandLogs = commandLogs;

		jda.addEventListener(new ListenerAdapter() {
			@Override
			public void onButtonClick(@NotNull ButtonClickEvent event) {
				if (!event.getComponentId().startsWith("msg:")) return;
				Objects.requireNonNull(event.getMember()).getUser().openPrivateChannel().queue(
						privateChannel -> {
							privateChannel.sendMessage(Objects.requireNonNull(event.getMessage()).getEmbeds().get(0)).queue();
						}
				);
				event.reply("Kopie van deze embed is gestuurd naar je PM!").queue(q -> q.setEphemeral(true));
			}
		});
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerCommandPreprocessEvent.class)
				.handler(e -> {
					sendEmbed(commandLogs, e.getPlayer().getName(), e.getMessage()).queue();
				}).bindWith(consumer);
		Events.subscribe(ServerCommandEvent.class)
				.handler(e -> {
					sendEmbed(commandLogs, "Console", e.getCommand()).queue();
				}).bindWith(consumer);
	}

	public MessageAction sendEmbed(long id, String issuer, String command) {
		return Objects.requireNonNull(this.jda.getTextChannelById(id))
				.sendMessage(new EmbedBuilder()
				.setTitle("Command")
				.addField("Speler", issuer, true)
				.addField("Inhoud", command, true)
				.addField("Tijdstip", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:ms").format(new Date()), true)
				.build()).setActionRow(Button.danger("msg:" + issuer + ":" + command, "Stuur naar PM"));
	}

}
