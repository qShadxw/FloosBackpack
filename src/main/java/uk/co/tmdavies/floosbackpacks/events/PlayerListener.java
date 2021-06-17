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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import uk.co.tmdavies.floosbackpacks.FloosBackpacks;
import uk.co.tmdavies.floosbackpacks.utils.Config;

import java.util.HashMap;
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

}
