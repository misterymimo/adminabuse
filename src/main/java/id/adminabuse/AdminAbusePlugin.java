package id.adminabuse;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class AdminAbusePlugin extends JavaPlugin implements Listener {

    private static final int TOTAL_SECONDS = 15 * 60; // 15 menit

    private BossBar bar;
    private BukkitTask task;
    private int remaining;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        stop();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("stop")) {
            if (bar == null) {
                sender.sendMessage(Component.text("Admin abuse lagi nggak jalan.", NamedTextColor.RED));
            } else {
                stop();
                Bukkit.getServer().sendMessage(
                        Component.text("Admin abuse dihentikan.", NamedTextColor.YELLOW));
            }
            return true;
        }
        start();
        return true;
    }

    private void start() {
        stop(); // reset kalau lagi jalan

        remaining = TOTAL_SECONDS;
        bar = BossBar.bossBar(title(), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        Bukkit.getServer().showBossBar(bar);

        Bukkit.getServer().sendMessage(
                Component.text("ADMIN ABUSE dimulai! Durasi 15 menit.", NamedTextColor.RED));

        task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            remaining--;
            if (remaining <= 0) {
                stop();
                Bukkit.getServer().sendMessage(
                        Component.text("Admin abuse selesai!", NamedTextColor.GREEN));
                return;
            }
            bar.progress((float) remaining / TOTAL_SECONDS);
            bar.name(title());
        }, 20L, 20L);
    }

    private void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (bar != null) {
            Bukkit.getServer().hideBossBar(bar);
            bar = null;
        }
    }

    private Component title() {
        int m = remaining / 60;
        int s = remaining % 60;
        return Component.text(String.format("ADMIN ABUSE • %02d:%02d", m, s), NamedTextColor.RED);
    }

    // pemain yang join di tengah event tetap lihat bar-nya
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (bar != null) {
            e.getPlayer().showBossBar(bar);
        }
    }
}
