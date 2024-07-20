package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.EasyClansResponse;
import nl.me.easyclans.helpers.Status;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.ColorUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.create") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.create");
        if (args.length == 1) return MessageUtils.onWrongUsage(player, "/clan create <name>");
        if (args.length == 2) {
            if (PlayerUtils.hasClan(player)) return MessageUtils.onAlreadyInClan(player, "clan");
            List<String> clanNames = ClanUtils.getAlLClanNames();
            if (clanNames.contains(args[1])) return MessageUtils.onClanAlreadyExists(player, args[1]);
            if (!ColorUtils.isNameOrPrefixAllowed(args[1])) return MessageUtils.onInvalidCodes(player);
            ClanDTO clanDTO = new ClanDTO();
            Map<UUID, Boolean> members = new HashMap<>();
            members.put(player.getUniqueId(), true);
            clanDTO.setClanId(UUID.randomUUID());
            clanDTO.setName(args[1]);
            clanDTO.setOwner(player.getUniqueId());
            clanDTO.setMembers(members);

            EasyClansResponse response = ClanUtils.createClan(clanDTO);
            if (response.getStatus() == Status.SUCCESS) {
                MessageUtils.onClanCreated(player, clanDTO.getName());
                PlayerUtils.setClan(player, clanDTO.getClanId());
            } else {
                String message = response.getMessage();
                if (message.equalsIgnoreCase("CLAN_ALREADY_EXISTS")) return MessageUtils.onClanAlreadyExists(player, clanDTO.getName());
                else return MessageUtils.onUnknownError(player);
            }
        }
        return true;
    }
}
