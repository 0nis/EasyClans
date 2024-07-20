package nl.me.easyclans.helpers;

import nl.me.easyclans.helpers.utils.InviteUtils;
import org.bukkit.Bukkit;

public class ExpirationThread {

    public static void start() {
        Thread thread = new Thread(() -> {
            while (true) {
                InviteUtils.removeExpiredInvites();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Bukkit.getLogger().warning("[EasyClans] Thread interrupted!");
                }
            }
        });
        thread.start();
    }
}