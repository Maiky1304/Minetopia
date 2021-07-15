package dev.maiky.minetopia.modules.plots.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.plots.listener.PlotWandListener;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@CommandAlias("plotwand")
@Description("Krijg een plotwand")
@CommandPermission("minetopia.plotwand")
public class PlotWandCommand extends BaseCommand {

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender) {
        Minetopia.showHelp(sender, this, getSubCommands());
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage(Message.COMMON_COMMAND_UNKNOWNSUBCOMMAND.raw());
        this.onHelp(sender);
    }

    @Override
    public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
        issuer.sendMessage(Message.COMMON_COMMAND_SYNTAX.format(getExecCommandLabel(), cmd.getPrefSubCommand(), cmd.getSyntaxText()));
    }

    @Subcommand("clear")
    @Description("Clear je selectie")
    public void onClear(Player player) {
        if (!PlotWandListener.getHashMap().containsKey(player)) {
            throw new ConditionFailedException("Je hebt geen actieve selectie!");
        }

        PlotWandListener.getHashMap().remove(player);
        player.sendMessage(Text.colors("&3Je selectie is gecleared."));
    }

    @Subcommand("krijg")
    @Description("Krijg een plotwand")
    public void onKrijg(Player player) {
        ItemStack itemStack = ItemStackBuilder.of(Material.STICK)
                .name("&3PlotWand").lore("&bLinkerklik &7voor positie #1", "&bRechterklik &7voor positie #2").build();
        while (player.getInventory().contains(itemStack)) {
            player.getInventory().remove(itemStack);
        }
        player.getInventory().addItem(itemStack);
        player.sendMessage("§3Je hebt een §bplotwand §3ontvangen!");
    }

}
