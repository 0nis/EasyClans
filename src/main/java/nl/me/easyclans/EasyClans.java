package nl.me.easyclans;

import nl.me.easyclans.commands.ClanChatCommand;
import nl.me.easyclans.commands.ClanCommand;
import nl.me.easyclans.commands.admin_commands.GetClanIDCommand;
import nl.me.easyclans.configs.ClanConfig;
import nl.me.easyclans.configs.MessagesConfig;
import nl.me.easyclans.configs.PlayerConfig;
import nl.me.easyclans.helpers.ExpirationThread;
import nl.me.easyclans.integrations.blocklocker.EasyClansGroupSystem;
import nl.me.easyclans.integrations.discordsrv.DiscordSRVUtils;
import nl.me.easyclans.listeners.EntityDamageByEntityListener;
import nl.me.easyclans.integrations.placeholderapi.EasyClansPlaceholders;
import nl.rutgerkok.blocklocker.BlockLockerAPIv2;
import nl.rutgerkok.blocklocker.group.GroupSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.List;

public final class EasyClans extends JavaPlugin {

    private static EasyClans plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;
        Bukkit.getLogger().info("[EasyClans] Plugin has been enabled!");

        // Integration with PlaceholderAPI for placeholders
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("[EasyClans] PlaceholderAPI not found! Some features may not work.");
        } else {
            getLogger().info("[EasyClans] PlaceholderAPI found! Hooking into PlaceholderAPI...");
            new EasyClansPlaceholders().register();
        }

        // Integration with BlockLocker for clan chest protection
        if (Bukkit.getPluginManager().getPlugin("BlockLocker") == null) {
            getLogger().warning("[EasyClans] BlockLocker not found! Some features may not work.");
        } else {
            getLogger().info("[EasyClans] BlockLocker found! Hooking into BlockLocker...");
            BlockLockerAPIv2.getPlugin().getGroupSystems().addSystem(new EasyClansGroupSystem());
//            try {
//                Class<?> groupSystemClass = Class.forName("nl.rutgerkok.blocklocker.BlockLockerAPIv2");
//                Method addSystemMethod = groupSystemClass.getMethod("addSystem", GroupSystem.class);
//                addSystemMethod.invoke(null, new EasyClansGroupSystem());
//                getLogger().info("[EasyClans] BlockLocker found! Hooking into BlockLocker...");
//            } catch (Exception e) {
//                getLogger().warning("[EasyClans] Failed to hook into BlockLocker. Is the plugin installed?");
//            }
        }

        // Integration with DiscordSRV for clan chats and admin clan chat moderation
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") == null) {
            getLogger().warning("[EasyClans] DiscordSRV not found! Some features may not work.");
        } else {
            getLogger().info("[EasyClans] DiscordSRV found! Hooking into DiscordSRV...");
            DiscordSRVUtils.subscribeToDiscordSRV();
        }

        saveDefaultConfig();

        PlayerConfig.setup();
        PlayerConfig.get().options().copyDefaults(true);
        PlayerConfig.save();

        ClanConfig.setup();
        ClanConfig.get().options().copyDefaults(true);
        ClanConfig.save();

        MessagesConfig.setup();
        MessagesConfig.get().options().copyDefaults(true);
        MessagesConfig.save();

        setCommandExecutor("clan", new ClanCommand());
        setCommandExecutor("clanchat", new ClanChatCommand());
        setCommandExecutor("clan-id", new GetClanIDCommand());
        registerEvent(new EntityDamageByEntityListener());

        ExpirationThread.start();
    }

    private void setCommandExecutor(String command, CommandExecutor executor) {
        PluginCommand pluginCommand = plugin.getCommand(command);
        if (pluginCommand != null) pluginCommand.setExecutor(executor);
        else Bukkit.getLogger().warning("Could not set command executor for " + command + "!");
    }

    private void registerEvent(Listener listener) {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EasyClans getPlugin() {
        return plugin;
    }
}
