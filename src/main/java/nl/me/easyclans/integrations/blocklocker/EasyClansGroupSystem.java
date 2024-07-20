package nl.me.easyclans.integrations.blocklocker;

import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.rutgerkok.blocklocker.group.GroupSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class EasyClansGroupSystem extends GroupSystem {
    @Override
    public boolean isInGroup(Player player, String groupName) {
        if (player == null) return false;
        UUID clanid = ClanUtils.getClanUUIDFromName(groupName);
        if (clanid == null) return false;
        if (ClanUtils.isMember(clanid, player.getUniqueId())) return true;
        List<String> allies = ClanUtils.getClanAllies(clanid);
        if (allies == null || allies.size() == 0) return false;
        for (String ally : allies) {
            if (ClanUtils.isMember(UUID.fromString(ally), player.getUniqueId())) return true;
        }
        return false;
    }
}
