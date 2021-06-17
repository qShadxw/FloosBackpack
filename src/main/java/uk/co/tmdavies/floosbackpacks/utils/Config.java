package uk.co.tmdavies.floosbackpacks.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private File configFile;
    private YamlConfiguration config;

    public Config(String name) {

        configFile = new File("./plugins/FloosBackpacks/" + name + ".yml");

        if (!configFile.getParentFile().exists()) configFile.getParentFile().mkdirs();

        if (!configFile.exists()) {

            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        config = YamlConfiguration.loadConfiguration(configFile);

    }

    public void set(String path, Object obj) {

        config.set(path, obj);

    }

    public Object get(String path) {

        return config.get(path);

    }

    public void saveConfig() {

        try {

            config.save(configFile);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void reloadConfig() {

        try {

            config.load(configFile);

        } catch (IOException | InvalidConfigurationException e) {

            e.printStackTrace();

        }

    }

}