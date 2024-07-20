package nl.me.easyclans.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ClanConfig {

    private static File file;
    private static FileConfiguration clanConfig;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("EasyClans").getDataFolder(), "clans.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[EasyClans] Could not create clans.yml!");
                Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
            }
        }
        clanConfig = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized static FileConfiguration get() {
        return clanConfig;
    }

    public synchronized static void save() {
        try {
            clanConfig.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[EasyClans] Could not save clans.yml!");
            Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
        }
    }

    public synchronized static void reload() {
        try {
            clanConfig = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[EasyClans] Could not reload clans.yml!");
            Bukkit.getLogger().severe("[EasyClans] Error: " + e.getMessage());
        }
    }

}
