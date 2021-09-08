/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.cosmicity.nsys;

import ch.jalu.configme.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.cosmicity.nsys.commands.DelHomeC;
import xyz.cosmicity.nsys.commands.HomeC;
import xyz.cosmicity.nsys.commands.HomesC;
import xyz.cosmicity.nsys.commands.SetHomeC;
import xyz.cosmicity.nsys.config.SettingsHandler;
import xyz.cosmicity.nsys.config.settings.StartupSettings;
import xyz.cosmicity.nsys.listeners.LoadingListener;
import xyz.cosmicity.nsys.storage.SQLService;
import xyz.cosmicity.nsys.storage.SQLUtils;

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
        getLogger().info("Disabling NeboStats...");
        sql.onDisable();
        SQLUtils.close();
    }

    public SQLService sql() {
        return sql;
    }
}
