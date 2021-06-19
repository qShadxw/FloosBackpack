package uk.co.tmdavies.floosbackpacks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.tmdavies.floosbackpacks.commands.BackpackCommand;
import uk.co.tmdavies.floosbackpacks.events.PlayerListener;
import uk.co.tmdavies.floosbackpacks.utils.Config;
import uk.co.tmdavies.floosbackpacks.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FloosBackpacks extends JavaPlugin {

    public Config config;
    public Config lang;
    public Config data;

    public double configVer = 1.0;
    public double langVer = 1.1;

    public HashMap<String, Inventory> backpackStorage;
    public HashMap<Player, String> checkingBackpack;
    public List<Player> onlinePlayers;

    @Override
    public void onEnable() {

        new Utils(this);

        backpackStorage = new HashMap<>();
        checkingBackpack = new HashMap<>();
        onlinePlayers = new ArrayList<>();

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

    @Override
    public void onDisable() {

        for (Player p : onlinePlayers) {

            Utils.saveBackpacks(p);

            onlinePlayers.remove(p);

        }

        onlinePlayers.clear();

    }

    public void setUpConfig() {

        if (config.getConfig().getDouble("Version") != configVer) {

            config.set("Version", configVer);
            config.set("Backpack.Name", "&6&lBackpack");
            config.set("Backpack.Material", "SKULL_ITEM");
            config.set("Backpack.SkinUrl", "8351e505989838e27287e7afbc7f97e796cab5f3598a76160c131c940d0c5");

            config.saveConfig();

        }

    }

    public void setUpLang() {

        if (lang.getConfig().getDouble("Version") != langVer) {

            lang.set("Version", langVer);
            lang.set("Prefix", "&6&lFloosBackpack &8&l»");
            lang.set("Backpack.Give", "%prefix% &e%sender% &7has given you a backpack.");
            lang.set("Backpack.Not-Existing", "%prefix% &cThe backpack with the id %id% does not exist.");
            lang.set("Misc.No-Permission", "%prefix% &cYou do not have the permission: &4%permission%&c.");
            lang.set("Misc.Target-Offline", "%prefix% &cThat player is offline.");
            lang.set("Misc.Invalid-Size", "%prefix% &cThe size you specified is invalid.");

            lang.saveConfig();

        }

    }

}
