package dev.maiky.minetopia.modules.discord.listeners;

import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Door: Maiky
 * Info: Minetopia - 06 Jun 2021
 * Package: dev.maiky.minetopia.modules.discord.listeners
 */

public class MinecraftCommandLogger implements Listener {

	private final JDA jda;

	public MinecraftCommandLogger(JDA jda) {
		this.jda = jda;

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

	public void register() {
		Events.subscribe(PlayerCommandPreprocessEvent.class)
				.handler(e -> {
					sendEmbed(736639994760003594L, e.getPlayer().getName(), e.getMessage()).queue();
				});
		Events.subscribe(ServerCommandEvent.class)
				.handler(e -> {
					sendEmbed(736639994760003594L, "Console", e.getCommand()).queue();
				});
	}

	public MessageAction sendEmbed(long id, String issuer, String command) {
		return Objects.requireNonNull(this.jda.getTextChannelById(id))
				.sendMessage(new EmbedBuilder()
				.setTitle("Command")
				.addField("Issuer", issuer, true)
				.addField("Content", command, true)
				.build()).setActionRow(Button.danger("msg:" + issuer + ":" + command, "Stuur naar PM"));
	}

}
