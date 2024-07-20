package nl.me.easyclans.commands;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.integrations.discordsrv.DiscordSRVUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClanChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return MessageUtils.onMustBePlayer(commandSender);
        Player player = (Player) commandSender;
        if (args.length < 1) return MessageUtils.onWrongUsage(player, "/cc <message>");
        String message = String.join(" ", args);
        return MessageUtils.onClanChatMessage(player, message);
    }
}
