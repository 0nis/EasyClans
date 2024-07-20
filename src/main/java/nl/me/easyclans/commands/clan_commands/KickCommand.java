package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KickCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.kick") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.kick");
        if (args.length != 2) return MessageUtils.onWrongUsage(player, "/clan kick <player>");
        if (args[1] == null || args[1].isEmpty()) return MessageUtils.onWrongUsage(player, "/clan kick <player>");

        ClanDTO clan = PlayerUtils.getClan(player);
        if (clan == null) return MessageUtils.onMustBeMember(player);
        if (!PlayerUtils.isPlayerLeader(player, clan)) return MessageUtils.onMustBeLeader(player);

        UUID targetUUID = PlayerUtils.getPlayerUUIDFromName(args[1]);
        if (targetUUID == null) return MessageUtils.onPlayerNotFound(player, args[1]);
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
        if (target == null) return MessageUtils.onPlayerNotFound(player, args[1]);

        if (clan.getOwner().toString().equals(targetUUID.toString())) return MessageUtils.onCannotKickOwner(player, clan.getName());
        if (player.getUniqueId().toString().equals(targetUUID.toString())) return MessageUtils.onCannotKickSelf(player, clan.getName());

        clan.getMembers().forEach((member, isLeader) -> {
            if (member.toString().equalsIgnoreCase(targetUUID.toString())) {
                PlayerUtils.removeClan(target.getUniqueId());
                ClanUtils.removeMember(clan.getClanId(), targetUUID);
            }
        });
        MessageUtils.onClanMemberRemoved(player, clan.getName(), target.getName());
        if (target.isOnline()) MessageUtils.onClanKicked(player, target.getPlayer(), clan.getName());
        return true;
    }
}
