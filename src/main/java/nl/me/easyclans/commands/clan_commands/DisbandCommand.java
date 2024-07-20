package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.EasyClansResponse;
import nl.me.easyclans.helpers.Status;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class DisbandCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.disband") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.disband");
        if (args.length == 1) {
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return MessageUtils.onMustBeMember(player);
            if (!(clan.getOwner().toString()).equals(player.getUniqueId().toString())) return MessageUtils.onMustBeOwner(player);
            EasyClansResponse response = ClanUtils.deleteClan(clan.getClanId());
            if (response.getStatus() == Status.SUCCESS) {
                PlayerUtils.kickAllPlayersOutOfClan(clan.getClanId());
                return MessageUtils.onClanDeleted(player, clan.getName());
            } else {
                return MessageUtils.onUnknownError(player);
            }
        } else {
            return MessageUtils.onWrongUsage(player, "/clan disband [clan]");
        }
    }
}
