package nl.me.easyclans.commands.admin_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GetClanIDCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("easyclans.admin.getclanid") && !commandSender.isOp()) return MessageUtils.onNoPermission(commandSender, "easyclans.admin.getclanid");
        if (args.length < 1) return MessageUtils.onWrongUsage(commandSender, "/clan-id <clan>");
        String clanName = args[0];
        ClanDTO clan = ClanUtils.getClan(clanName);
        if (clan == null) return MessageUtils.onClanDoesNotExist(commandSender, clanName);
        if (!clan.getName().equals(clanName)) return MessageUtils.onClanDoesNotExist(commandSender, clanName);
        commandSender.sendMessage(clan.getClanId().toString());
        Bukkit.getLogger().info("Clan ID for clan " + clanName + " is " + clan.getClanId().toString());
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return ClanUtils.getAlLClanNames();
    }
}
