package nl.me.easyclans.integrations.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaceholderAPIUtils {

    /**
     * Formats a message with placeholders from PlaceholderAPI if it is installed
     * @param commandSender The command sender to replace the placeholders for (null if console)
     * @param message The message to format
     * @return The formatted message
     */
    public static String setPlaceholders(CommandSender commandSender, String message) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if (commandSender instanceof Player) message = PlaceholderAPI.setPlaceholders((Player) commandSender, message);
            else message = PlaceholderAPI.setPlaceholders(null, message);
        }
        return message;
    }

}
