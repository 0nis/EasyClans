package nl.me.easyclans.commands.clan_commands.settings;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DemoteCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.demote") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.demote");
        ClanDTO clan = PlayerUtils.getClan(player);
        if (clan == null) return MessageUtils.onMustBeMember(player);
        if (!PlayerUtils.isPlayerLeader(player, clan)) return MessageUtils.onMustBeLeader(player);
        if (args.length == 2) {
            String targetName = args[1];
            if (targetName == null) return MessageUtils.onWrongUsage(player, "/clan demote <player>");
            UUID targetUUID = PlayerUtils.getPlayerUUIDFromName(targetName);
            if (targetUUID == null) return MessageUtils.onPlayerNotFound(player, targetName);
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            if (target == null) return MessageUtils.onPlayerNotFound(player, targetName);
            if (!ClanUtils.isMember(clan.getClanId(), targetUUID)) return MessageUtils.onPlayerNotInClan(player, targetName);
            if (!PlayerUtils.isPlayerLeader(target, clan)) return MessageUtils.onPlayerNotALeader(player, clan.getName(), targetName);
            ClanUtils.setLeader(clan.getClanId(), targetUUID, false);
            return MessageUtils.onClanLeaderRemoved(player, clan.getName(), targetName);
        } else {
            return MessageUtils.onWrongUsage(player, "/clan demote <player>");
        }
    }
}
