package pl.msurvival.welcome;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MSurvivalWelcome extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("MSurvivalWelcome wlaczony!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (getConfig().getBoolean("hide-default-join-message", true)) {
            event.joinMessage(null);
        }

        long delay = 0L;

        for (String line : getConfig().getStringList("welcome-message")) {
            if (line.startsWith("DELAY:")) {
                try {
                    delay += Long.parseLong(line.substring(6).trim());
                } catch (NumberFormatException ignored) {}
                continue;
            }

            String message = color(line.replace("%player%", player.getName()));
            long finalDelay = delay;

            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.sendMessage(message);
            }, finalDelay);
        }

        if (getConfig().getBoolean("title.enabled", true)) {
            String title = color(getConfig().getString("title.title", "&6&lMSURVIVAL"))
                    .replace("%player%", player.getName());
            String subtitle = color(getConfig().getString("title.subtitle", "&eWitaj na serwerze!"))
                    .replace("%player%", player.getName());

            player.sendTitle(title, subtitle, 10, 60, 20);
        }

        if (getConfig().getBoolean("sound.enabled", true)) {
            try {
                Sound sound = Sound.valueOf(getConfig().getString("sound.name", "ENTITY_PLAYER_LEVELUP"));
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (Exception ignored) {}
        }
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
