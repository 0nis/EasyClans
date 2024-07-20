package nl.me.easyclans.commands.clan_commands.settings;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.EasyClansResponse;
import nl.me.easyclans.helpers.Status;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class AllyCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.ally") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.ally");
        if (args.length != 3) return MessageUtils.onWrongUsage(player, "/clan ally <add|remove> <clan>");
        ClanDTO clan = PlayerUtils.getClan(player);
        if (clan == null) return MessageUtils.onMustBeMember(player);
        if (!PlayerUtils.isPlayerLeader(player, clan)) return MessageUtils.onMustBeLeader(player);

        String clanName = args[2];
        if (clanName.equalsIgnoreCase(clan.getName())) return MessageUtils.onCannotAllyYourself(player, clanName);
        ClanDTO allyClan = ClanUtils.getClan(clanName);
        if (allyClan == null) return MessageUtils.onClanDoesNotExist(player, clanName);

        if (args[1].equalsIgnoreCase("add")) {
            if (!player.hasPermission("easyclans.clan.ally.add") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.ally.add");
            EasyClansResponse response = ClanUtils.addAlly(clan.getClanId(), allyClan.getClanId());
            if (response.getStatus() == Status.SUCCESS) {
                return MessageUtils.onClanAllyAdded(player, clan.getName(), allyClan.getName());
            } else {
                String message = response.getMessage();
                if (message.equalsIgnoreCase("ALLY_ALREADY_EXISTS")) return MessageUtils.onClanAlreadyAllied(player, clan.getName(), allyClan.getName());
                else return MessageUtils.onUnknownError(player);
            }
        } else if (args[1].equalsIgnoreCase("remove")) {
            if (!player.hasPermission("easyclans.clan.ally.remove") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.ally.remove");
            EasyClansResponse response = ClanUtils.removeAlly(clan.getClanId(), allyClan.getClanId());
            if (response.getStatus() == Status.SUCCESS) {
                return MessageUtils.onClanAllyRemoved(player, clan.getName(), allyClan.getName());
            } else {
                String message = response.getMessage();
                if (message.equalsIgnoreCase("ALLY_NOT_FOUND")) return MessageUtils.onClanNotAllied(player, clan.getName(), allyClan.getName());
                if (message.equalsIgnoreCase("NO_ALLIES_FOUND")) return MessageUtils.onNoAlliesFound(player, clan.getName());
                else return MessageUtils.onUnknownError(player);
            }
        } else {
            return MessageUtils.onWrongUsage(player, "/clan ally <add|remove> <clan>");
        }
    }
}
