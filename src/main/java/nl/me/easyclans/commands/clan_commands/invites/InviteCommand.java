package nl.me.easyclans.commands.clan_commands.invites;

import nl.me.easyclans.EasyClans;
import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.InviteUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class InviteCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.invite") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.invite");
        if (args.length < 2) return MessageUtils.onWrongUsage(player, "/clan invite <player>");
        String playerName = args[1];
        if (playerName == null || playerName.isEmpty()) return MessageUtils.onWrongUsage(player, "/clan invite <player>");
        Player invitedPlayer = player.getServer().getPlayer(playerName);
        if (invitedPlayer == null || !invitedPlayer.isOnline()) return MessageUtils.onPlayerNotFound(player, playerName);
        ClanDTO clan = PlayerUtils.getClan(player);
        if (clan == null) return MessageUtils.onMustBeMember(player);
        if (!PlayerUtils.isPlayerLeader(player, clan)) return MessageUtils.onMustBeLeader(player);
        int maxMembers = EasyClans.getPlugin().getConfig().getInt("maxMembers");
        if (clan.getMembers().size() >= maxMembers) return MessageUtils.onClanMaxMembersReached(player, clan.getName(), maxMembers);
        InviteUtils.addInvite(clan.getClanId(), player.getUniqueId(), invitedPlayer.getUniqueId());
        return MessageUtils.onClanInviteCreated(player, invitedPlayer, clan.getName());
    }
}
