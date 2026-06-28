package pl.msurvival.welcome;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
                } catch (NumberFormatException ignored) {
                }
                continue;
            }

            String message = color(line.replace("%player%", player.getName()));
            long finalDelay = delay;

            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (player.isOnline()) {
                    player.sendMessage(message);
                }
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
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = command.getName().toLowerCase();

        if (name.equals("pomoc") || name.equals("komendy")) {
            sendHelp(sender);
            return true;
        }

        if (name.equals("welcomereload")) {
            if (!sender.hasPermission("msurvivalwelcome.admin")) {
                sender.sendMessage(color(getConfig().getString("messages.no-permission", "&cNie masz uprawnień.")));
                return true;
            }

            reloadConfig();
            sender.sendMessage(color(getConfig().getString("messages.reload", "&aPrzeładowano konfigurację.")));
            return true;
        }

        return false;
    }

    private void sendHelp(CommandSender sender) {
        for (String line : getConfig().getStringList("help-message")) {
            sender.sendMessage(color(line));
        }
    }

    private String color(String text) {
        if (text == null) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
