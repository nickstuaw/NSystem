/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.nsgw.nsys;

import ch.jalu.configme.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nsgw.nsys.commands.DelHomeC;
import xyz.nsgw.nsys.commands.HomeC;
import xyz.nsgw.nsys.commands.HomesC;
import xyz.nsgw.nsys.commands.SetHomeC;
import xyz.nsgw.nsys.config.SettingsHandler;
import xyz.nsgw.nsys.config.settings.StartupSettings;
import xyz.nsgw.nsys.listeners.LoadingListener;
import xyz.nsgw.nsys.storage.SQLService;
import xyz.nsgw.nsys.storage.SQLUtils;

import java.util.Objects;

public final class NSys extends JavaPlugin {
    private SQLService sql;
    private SettingsHandler settingsHandler;

    @Override
    public void onEnable() {
        getLogger().info("Enabling NSys...");
        settingsHandler = new SettingsHandler(this.getDataFolder());
        SettingsManager startup = settingsHandler.startup();
        sql = new SQLService(
                startup.getProperty(StartupSettings.MYSQL_HOST),
                startup.getProperty(StartupSettings.MYSQL_DB),
                startup.getProperty(StartupSettings.MYSQL_USER),
                startup.getProperty(StartupSettings.MYSQL_PASS));

        Bukkit.getPluginManager().registerEvents(new LoadingListener(this), this);

        Objects.requireNonNull(Bukkit.getPluginCommand("home")).setExecutor(new HomeC(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("homes")).setExecutor(new HomesC(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("sethome")).setExecutor(new SetHomeC(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("delhome")).setExecutor(new DelHomeC(this));

    }

    public SettingsManager getGenSettings() {
        return settingsHandler.gen();
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling NSys...");
        sql.onDisable();
        SQLUtils.close();
    }

    public SQLService sql() {
        return sql;
    }
}
