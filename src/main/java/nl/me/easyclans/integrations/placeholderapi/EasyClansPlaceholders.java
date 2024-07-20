package nl.me.easyclans.integrations.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.me.easyclans.EasyClans;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.ColorUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EasyClansPlaceholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "easyclans";
    }

    @Override
    public @NotNull String getAuthor() {
        return EasyClans.getPlugin().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return EasyClans.getPlugin().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("clan-prefix")) {
            if (player == null) return "";
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return "";
            return ColorUtils.formatPrefix(clan.getPrefix());
        } else if (params.equalsIgnoreCase("clan-name")) {
            if (player == null) return "";
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return "";
            return clan.getName();
        } else if (params.equalsIgnoreCase("clan-isLeader")) {
            if (player == null) return "";
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return "";
            return PlayerUtils.isPlayerLeader(player, clan) ? "true" : "false";
        } else if (params.equalsIgnoreCase("clan-isOwner")) {
            if (player == null) return "";
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return "";
            return (player.getUniqueId().toString().equalsIgnoreCase(clan.getOwner().toString())) ? "true" : "false";
        } else if (params.equalsIgnoreCase("clan-home")) {
            if (player == null) return "";
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return "";
            Location home = clan.getHome();
            if (home == null) return "";
            String homeString = "";
            homeString = home.getWorld().getName() + ", " + home.getBlockX() + " " + home.getBlockY() + " " + home.getBlockZ();
            return homeString;
        } else if (params.equalsIgnoreCase("total-clans")) {
            List<String> clans = ClanUtils.getAlLClanNames();
            if (clans == null || clans.isEmpty()) return "0";
            return String.valueOf(clans.size());
        }
        return null;
    }
}
