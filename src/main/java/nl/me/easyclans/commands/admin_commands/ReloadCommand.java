package nl.me.easyclans.commands.admin_commands;

import nl.me.easyclans.EasyClans;
import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.configs.ClanConfig;
import nl.me.easyclans.configs.MessagesConfig;
import nl.me.easyclans.configs.PlayerConfig;
import nl.me.easyclans.helpers.utils.MessageUtils;
import org.bukkit.entity.Player;

public class ReloadCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.admin.reload") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.admin.reload");
        try {
            EasyClans.getPlugin().reloadConfig();
            ClanConfig.reload();
            MessagesConfig.reload();
            PlayerConfig.reload();
        } catch (Exception e) {
            return MessageUtils.onUnknownError(player);
        }
        return MessageUtils.onConfigReloaded(player);
    }
}
