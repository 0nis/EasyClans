package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.EasyClansResponse;
import nl.me.easyclans.helpers.Status;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class LeaveCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.leave") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.leave");
        if (args.length == 1) {
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return MessageUtils.onMustBeMember(player);
            if ((clan.getOwner().toString()).equals(player.getUniqueId().toString())) return MessageUtils.onCantLeaveBecauseOwner(player, clan.getName());

            EasyClansResponse response = ClanUtils.removeMember(clan.getClanId(), player.getUniqueId());
            if (response.getStatus() == Status.SUCCESS) {
                PlayerUtils.removeClan(player.getUniqueId());
                MessageUtils.onPlayerHasLeftClan(player, clan.getName());
                return MessageUtils.onClanLeave(player, clan.getName());
            } else {
                if (response.getMessage().equals("CLAN_NOT_FOUND")) return MessageUtils.onClanDoesNotExist(player, clan.getName());
                if (response.getMessage().equals("PLAYER_NOT_FOUND")) return MessageUtils.onPlayerNotFound(player, player.getName());
                else return MessageUtils.onUnknownError(player);
            }
        } else {
            return MessageUtils.onWrongUsage(player, "/clan leave");
        }
    }
}
