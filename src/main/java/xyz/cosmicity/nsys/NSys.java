/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.cosmicity.nsys;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.cosmicity.nsys.commands.DelHomeC;
import xyz.cosmicity.nsys.commands.HomeC;
import xyz.cosmicity.nsys.commands.HomesC;
import xyz.cosmicity.nsys.commands.SetHomeC;
import xyz.cosmicity.nsys.listeners.LoadingListener;
import xyz.cosmicity.nsys.storage.SQLService;
import xyz.cosmicity.nsys.storage.SQLUtils;

import java.util.Objects;

public final class NSys extends JavaPlugin {
    private SQLService sql;

    @Override
    public void onEnable() {
        getLogger().info("Enabling NSys...");
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        ConfigurationSection sqlConfig = getConfig().getConfigurationSection("mysql");
        if(sqlConfig == null) {
            getLogger().severe("MySQL settings are missing!");
        }
        assert sqlConfig != null;
        sql = new SQLService(sqlConfig.getString("host"),
                sqlConfig.getString("database"),
                sqlConfig.getString("user"), sqlConfig.getString("pass"));
        Bukkit.getPluginManager().registerEvents(new LoadingListener(this), this);

        Objects.requireNonNull(Bukkit.getPluginCommand("home")).setExecutor(new HomeC(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("homes")).setExecutor(new HomesC(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("sethome")).setExecutor(new SetHomeC(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("delhome")).setExecutor(new DelHomeC(this));
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
