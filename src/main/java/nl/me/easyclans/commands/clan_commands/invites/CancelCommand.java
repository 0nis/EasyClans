package nl.me.easyclans.commands.clan_commands.invites;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.dto.InviteDTO;
import nl.me.easyclans.helpers.utils.InviteUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class CancelCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.cancel") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.cancel");
        if (args.length < 2) return MessageUtils.onWrongUsage(player, "/clan cancel <player>");
        ClanDTO clan = PlayerUtils.getClan(player);
        if (clan == null) return MessageUtils.onMustBeMember(player);
        if (!PlayerUtils.isPlayerLeader(player, clan)) return MessageUtils.onMustBeLeader(player);
        String playerName = args[1];
        if (playerName == null || playerName.isEmpty()) return MessageUtils.onWrongUsage(player, "/clan cancel <player>");
        Player invitedPlayer = player.getServer().getPlayer(playerName);
        if (invitedPlayer == null || !invitedPlayer.isOnline()) return MessageUtils.onPlayerNotFound(player, playerName);
        InviteDTO invite = InviteUtils.getInvite(clan.getClanId(), invitedPlayer.getUniqueId());
        if (invite == null) return MessageUtils.onClanInviteNotSent(player, invitedPlayer, clan.getName());
        InviteUtils.removeInvite(invite.getClan(), invite.getInvitedPlayer());
        return MessageUtils.onClanInviteRemoved(player, invitedPlayer, clan.getName());
    }
}
