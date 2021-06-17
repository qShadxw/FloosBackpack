package uk.co.tmdavies.floosbackpacks;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.tmdavies.floosbackpacks.commands.BackpackCommand;
import uk.co.tmdavies.floosbackpacks.events.PlayerListener;
import uk.co.tmdavies.floosbackpacks.utils.Config;

import java.util.HashMap;
import java.util.UUID;

public final class FloosBackpacks extends JavaPlugin {

    public Config config;
    public Config lang;
    public Config data;

    public double configVer = 1.0;
    public double langVer = 1.0;

    public HashMap<UUID, Inventory> backpackStorage;

    @Override
    public void onEnable() {

        backpackStorage = new HashMap<>();

        // Load backpacks

        config = new Config("config");
        lang = new Config("lang");
        data = new Config("data");

        setUpConfig();
        setUpLang();

        new BackpackCommand(this);
        new PlayerListener(this);

        getLogger().info("FloosBackpacks");
        getLogger().info("Made by Carbonate");
        getLogger().info("Version: " + getDescription().getVersion());

    }

    public void setUpConfig() {

        if ((double) config.get("Version") != configVer || config.get("Version") == null) {

            config.set("Version", configVer);
            config.set("Backpack.Name", "&6&lBackpack");
            config.set("Backpack.Material", "LEATHER");

            config.saveConfig();

        }

    }

    public void setUpLang() {

        if ((double) lang.get("Version") != langVer || lang.get("Version") == null) {

            lang.set("Version", langVer);
            lang.set("Prefix", "&6&lFloosBackpack &8&l»");
            lang.set("Backpack.Give", "%prefix% &e%sender% &7has given you a backpack.");
            lang.set("Misc.Target-Offline", "%prefix% &cThat player is offline.");
            lang.set("Misc.Invalid-Size", "%prefix% &cThe size you specified is invalid.");

            lang.saveConfig();

        }

    }

}
