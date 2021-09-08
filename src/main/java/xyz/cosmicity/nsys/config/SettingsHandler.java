package xyz.cosmicity.nsys.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import xyz.cosmicity.nsys.config.settings.StartupSettings;

import java.io.File;

public class SettingsHandler {
    private final SettingsManager startupSettingsManager;
    private final SettingsManager generalSettingsManager;

    public SettingsHandler(File folder) {
        startupSettingsManager = SettingsManagerBuilder
            .withYamlFile(new File(folder,"startup_config.yml"))
            .configurationData(StartupSettings.class)
            .useDefaultMigrationService()
            .create();
        generalSettingsManager = SettingsManagerBuilder
            .withYamlFile(new File(folder,"general_config.yml"))
            .configurationData(StartupSettings.class)
            .useDefaultMigrationService()
            .create();
    }
    public SettingsManager startup() {
        return startupSettingsManager;
    }
    public SettingsManager gen() {
        return generalSettingsManager;
    }
}
