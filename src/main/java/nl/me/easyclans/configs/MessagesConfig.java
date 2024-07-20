package nl.me.easyclans.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesConfig {

    private static File file;
    private static FileConfiguration messageConfig;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("EasyClans").getDataFolder(), "messages.yml");
        if (!file.exists()) {
            try {
                Bukkit.getServer().getPluginManager().getPlugin("EasyClans").saveResource("messages.yml", false);
            } catch (Exception e) {
                Bukkit.getLogger().severe("[EasyClans] Could not create messages.yml!");
                Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
            }
        }
        messageConfig = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized static FileConfiguration get() {
        return messageConfig;
    }

    public synchronized static void save() {
        try {
            messageConfig.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[EasyClans] Could not save messages.yml!");
            Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
        }
    }

    public synchronized static void reload() {
        try {
            messageConfig = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[EasyClans] Could not reload messages.yml!");
            Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
        }
    }

}
