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
import java.util.Map;

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

        if (backpackStorage.keySet().isEmpty()) return;

        HashMap<Inventory, String> reverseStorage = new HashMap<>();

        for (Map.Entry entry : backpackStorage.entrySet())
            reverseStorage.put((Inventory) entry.getValue(), entry.getKey().toString());

        for (String id : reverseStorage.values()) {

            Inventory inv = backpackStorage.get(id);

            data.set(id + ".contents", inv.getContents());
            data.set(id + ".size", inv.getSize());
            data.saveConfig();

            backpackStorage.remove(id);

        }

    }

    public void setUpConfig() {

        if (config.getConfig().getDouble("Version") != configVer) {

            config.set("Version", configVer);
            config.set("Backpack.Name", "&6&lBackpack");
            config.set("Backpack.Material", "SKULL_ITEM");
            config.set("Backpack.SkinUrl", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWU1MDU5ODk4MzhlMjcyODdlN2FmYmM3Zjk3ZTc5NmNhYjVmMzU5OGE3NjE2MGMxMzFjOTQwZDBjNSJ9fX0=");

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
