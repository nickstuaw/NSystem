package xyz.nsgw.nsys.config.settings;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class StartupSettings implements SettingsHolder {
    // File heading
    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("",
                "These settings will not load when reloading the config.");
    }
    // MySQL
    public static final Property<String> MYSQL_HOST =
            newProperty("mysql.host", "not set");
    public static final Property<String> MYSQL_DB =
            newProperty("mysql.database", "database");
    public static final Property<String> MYSQL_USER =
            newProperty("mysql.username", "username");
    public static final Property<String> MYSQL_PASS =
            newProperty("mysql.password", "password");
    // Discord
    public static final Property<String> DC_TOKEN =
            newProperty("discord.token", "");
    public static final Property<String> DC_GUILD_ID =
            newProperty("discord.guild-id", "");
    public static final Property<String> DC_MOD_ROLE_ID =
            newProperty("discord.mod-role-id", "");
    public static final Property<String> DC_DM_OVERFLOW_CHANNEL_ID =
            newProperty("discord.dm-overflow-channel-id", "");

}
