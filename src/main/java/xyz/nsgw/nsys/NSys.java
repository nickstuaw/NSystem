/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.nsgw.nsys;

import ch.jalu.configme.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nsgw.nsys.commands.CommandHandler;
import xyz.nsgw.nsys.config.SettingsHandler;
import xyz.nsgw.nsys.config.settings.StartupSettings;
import xyz.nsgw.nsys.listeners.LoadingListener;
import xyz.nsgw.nsys.storage.objects.SettingsList;
import xyz.nsgw.nsys.storage.sql.SQLService;
import xyz.nsgw.nsys.storage.sql.SQLUtils;
import xyz.nsgw.nsys.utils.GUIHandler;

import java.util.logging.Logger;

public final class NSys extends JavaPlugin {
    private static SQLService sql;
    private static SettingsHandler settingsHandler;
    private static Logger logger;
    private CommandHandler commandHandler;
    private GUIHandler guiHandler;

    @Override
    public void onEnable() {

        getLogger().info("Enabling NSys...");

        setSettingsHandler(new SettingsHandler(this.getDataFolder()));

        SettingsManager startup = settingsHandler.startup();

        setSql(new SQLService(
                startup.getProperty(StartupSettings.MYSQL_HOST),
                startup.getProperty(StartupSettings.MYSQL_DB),
                startup.getProperty(StartupSettings.MYSQL_USER),
                startup.getProperty(StartupSettings.MYSQL_PASS)));

        Bukkit.getPluginManager().registerEvents(new LoadingListener(this), this);

        SettingsList warps = sql.wrapList("warps");
        sql.validateList(warps);

        guiHandler = new GUIHandler();

        commandHandler = new CommandHandler(this);

        setLogger(this.getServer().getLogger());

    }

    public SettingsManager getGenSettings() {
        return settingsHandler.gen();
    }

    public GUIHandler guiHandler() {return guiHandler;}

    @Override
    public void onDisable() {
        getLogger().info("Disabling NSys...");
        sql.invalidateList(sql.wrapList("warps"));
        commandHandler.onDisable();
        sql.onDisable();
        SQLUtils.close();
    }

    public static SQLService sql() {
        return sql;
    }

    private static void setSql(SQLService s) {
        sql = s;
    }

    public static SettingsHandler sh(){
        return settingsHandler;
    }

    private void setSettingsHandler(SettingsHandler handler) {
        settingsHandler = handler;
    }

    public static Logger log() {
        return logger;
    }
    private void setLogger(final Logger lgr) {
        logger = lgr;
    }
}
