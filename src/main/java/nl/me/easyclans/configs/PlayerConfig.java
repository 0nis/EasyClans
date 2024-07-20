package nl.me.easyclans.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerConfig {
    private static File file;
    private static FileConfiguration playerConfig;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("EasyClans").getDataFolder(), "players.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[EasyClans] Could not create players.yml!");
                Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
            }
        }
        playerConfig = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized static FileConfiguration get() {
        return playerConfig;
    }

    public synchronized static void save() {
        try {
            playerConfig.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[EasyClans] Could not save players.yml!");
            Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
        }
    }

    public synchronized static void reload() {
        try {
            playerConfig = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[EasyClans] Could not reload players.yml!");
            Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
        }
    }
}
