package nl.me.easyclans.helpers.utils;

import nl.me.easyclans.EasyClans;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class ColorUtils {

    /**
     * Formats the prefix with the allowed codes
     * @param prefix The prefix to format
     * @return The formatted prefix
     */
    public static String formatPrefix(String prefix) {
        ConfigurationSection config = EasyClans.getPlugin().getConfig();
        String prefixFormat = config.getString("prefixFormat");
        prefixFormat = ChatColor.translateAlternateColorCodes('&', prefixFormat);
        boolean allowColors = config.getBoolean("allowColorCodesInPrefix");
        boolean allowFormats = config.getBoolean("allowFormatCodesInPrefix");
        boolean allowMagic = config.getBoolean("allowMagicCodeInPrefix");
        if (allowColors) prefix = ColorUtils.setColorCodes(prefix);
        if (allowFormats) prefix = ColorUtils.setFormatCodes(prefix);
        if (allowMagic) prefix = ColorUtils.setMagicCode(prefix);
        prefix = prefixFormat.replace("{prefix}", prefix);
        return prefix;
    }

    /**
     * Whether the name of prefix is allowed based on whether the config allows it
     * @param nameOrPrefix The name or prefix to check
     * @return True if the name or prefix is allowed, false otherwise
     */
    public static boolean isNameOrPrefixAllowed(String nameOrPrefix) {
        ConfigurationSection config = EasyClans.getPlugin().getConfig();
        boolean allowColors = config.getBoolean("allowColorCodesInPrefix");
        boolean allowFormats = config.getBoolean("allowFormatCodesInPrefix");
        boolean allowMagic = config.getBoolean("allowMagicCodeInPrefix");
        boolean containsColorCodes = ColorUtils.containsColorCode(nameOrPrefix);
        boolean containsFormatCodes = ColorUtils.containsFormatCode(nameOrPrefix);
        boolean containsMagicCode = ColorUtils.containsMagicCode(nameOrPrefix);
        if (!allowColors && containsColorCodes) return false;
        if (!allowFormats && containsFormatCodes) return false;
        if (!allowMagic && containsMagicCode) return false;
        return true;
    }

    /**
     * Replaces all instances of &0-9, a-f and A-F with the corresponding color code
     * @param string The string to replace the color codes in
     * @return The string with the color codes replaced
     */
    public static String setColorCodes(String string) {
        string = string.replaceAll("&([0-9a-fA-F])", "\u00A7$1");
        return ChatColor.translateAlternateColorCodes('\u00A7', string);
    }

    /**
     * Replaces all instances of &l, &m, &n, &o and &r with the corresponding format code
     * @param string The string to replace the format codes in
     * @return The string with the format codes replaced
     */
    public static String setFormatCodes(String string) {
        string = string.replaceAll("&([lmnor])", "\u00A7$1");
        return ChatColor.translateAlternateColorCodes('\u00A7', string);
    }

    /**
     * Replaces all instances of &k with the corresponding magic code
     * @param string The string to replace the magic code in
     * @return The string with the magic code replaced
     */
    public static String setMagicCode(String string) {
        string = string.replaceAll("&([k])", "\u00A7$1");
        return ChatColor.translateAlternateColorCodes('\u00A7', string);
    }

    /**
     * Check if a string contains any color codes
     * @param string The string to check
     * @return True if the string contains any color codes, false otherwise
     */
    public static boolean containsColorCode(String string) {
        return string.matches(".*[&§][0-9a-fA-F].*");
    }

    /**
     * Check if a string contains any format codes (&l, &m, &n, &o, &r)
     * @param string The string to check
     * @return True if the string contains any format codes, false otherwise
     */
    public static boolean containsFormatCode(String string) {
        return string.matches(".*[&§][lmnor].*");
    }

    /**
     * Check if a string contains the magic code (&k)
     * @param string The string to check
     * @return True if the string contains the magic code, false otherwise
     */
    public static boolean containsMagicCode(String string) {
        return string.matches(".*[&§]k.*");
    }

}
