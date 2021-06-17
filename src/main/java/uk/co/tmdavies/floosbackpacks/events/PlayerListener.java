package uk.co.tmdavies.floosbackpacks.events;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import uk.co.tmdavies.floosbackpacks.FloosBackpacks;
import uk.co.tmdavies.floosbackpacks.utils.Config;
import uk.co.tmdavies.floosbackpacks.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final FloosBackpacks plugin;
    private Config config;
    private Config lang;
    private Config data;
    private HashMap<UUID, Inventory> backpackStorage;

    public PlayerListener(FloosBackpacks plugin) {

        this.plugin = plugin;
        this.config = plugin.config;
        this.lang = plugin.lang;
        this.data = plugin.data;
        this.backpackStorage = plugin.backpackStorage;

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) return;

        ItemStack hand = p.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(hand);

        String uuid = nbtItem.getString("id") != null ? nbtItem.getString("id") : null;

        if (uuid == null) return;

        p.openInventory(backpackStorage.get(UUID.fromString(uuid)));

        e.setCancelled(true);

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

        Player p = e.getPlayer();

        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) return;

        ItemStack hand = p.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(hand);

        String uuid = nbtItem.getString("id") != null ? nbtItem.getString("id") : null;

        if (uuid == null) return;

        p.openInventory(backpackStorage.get(UUID.fromString(uuid)));

        e.setCancelled(true);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player p = e.getPlayer();

        List<ItemStack> backPacks = new ArrayList<>();

        for (ItemStack item : p.getInventory().getContents()) {

            if (item.getType() == null || item.getType() == Material.AIR) continue;

            NBTItem nbtItem = new NBTItem(item);

            if (nbtItem.getString("id") != null) backPacks.add(item);

        }

        for (ItemStack item : backPacks) {

            NBTItem nbtItem = new NBTItem(item);
            String id = nbtItem.getString("id");
            Inventory inv = backpackStorage.get(UUID.fromString(id));

            data.set(id, inv.getContents());
            data.set(id + ".size", inv.getSize());
            data.saveConfig();

            backpackStorage.remove(UUID.fromString(id));

        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        List<ItemStack> backPacks = new ArrayList<>();

        for (ItemStack item : p.getInventory()) {

            if (item.getType() == null || item.getType() == Material.AIR) continue;

            NBTItem nbtItem = new NBTItem(item);

            if (nbtItem.getString("id") != null) backPacks.add(item);

        }

        for (ItemStack item : backPacks) {

            NBTItem nbtItem = new NBTItem(item);
            String id = nbtItem.getString("id");
            List<ItemStack> contents = (List<ItemStack>) data.getConfig().getList(id);

            ItemStack[] items = new ItemStack[contents.size()];
            for (int i = 0; i < contents.size(); i++) {
                ItemStack _i = contents.get(i);
                if (_i != null) {
                    items[i] = _i;
                } else {
                    items[i] = null;
                }
            }

            Inventory inv = Bukkit.createInventory(null, data.getConfig().getInt(id + ".size"),
                    Utils.Chat(config.getConfig().getString("Backpack.Name")));

            inv.setContents(items);

            backpackStorage.put(UUID.fromString(id), inv);

        }

    }

}
