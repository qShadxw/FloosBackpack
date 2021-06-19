package uk.co.tmdavies.floosbackpacks.events;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import uk.co.tmdavies.floosbackpacks.FloosBackpacks;
import uk.co.tmdavies.floosbackpacks.utils.Config;
import uk.co.tmdavies.floosbackpacks.utils.Utils;

import java.util.*;

public class PlayerListener implements Listener {

    private Config config;
    private Config data;
    private Config lang;
    private HashMap<String, Inventory> backpackStorage;
    private HashMap<Player, String> checkingBackpack;
    private List<Player> onlinePlayers;

    public PlayerListener(FloosBackpacks plugin) {

        this.config = plugin.config;
        this.data = plugin.data;
        this.lang = plugin.lang;
        this.backpackStorage = plugin.backpackStorage;
        this.checkingBackpack = plugin.checkingBackpack;
        this.onlinePlayers = plugin.onlinePlayers;

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        ItemStack hand = p.getInventory().getItemInMainHand();

        if (hand == null || hand.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(hand);

        if (nbtItem.getString("id").equals("")) return;

        String uuid = nbtItem.getString("id");

        p.sendMessage("Backpack: " + uuid);
        p.openInventory(backpackStorage.get(uuid));

        e.setCancelled(true);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player p = e.getPlayer();

        Utils.saveBackpacks(p);

        onlinePlayers.remove(p);

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        onlinePlayers.add(p);

        HashMap<ItemStack, String> backPacks = new HashMap<>();

        for (ItemStack item : p.getInventory()) {

            if (item == null) continue;

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.getString("id").equals("")) backPacks.put(item, nbtItem.getString("id"));

        }

        for (ItemStack item : backPacks.keySet()) {

            String id = backPacks.get(item);

            Inventory inv = Bukkit.createInventory(null, data.getConfig().getInt(id + ".size"),
                    Utils.Chat(config.getConfig().getString("Backpack.Name")));

            List<?> list = data.getConfig().getList(id + ".contents");

            if (list == null) {

                p.sendMessage(Utils.Chat(String.valueOf(lang.get("Backpack.Not-Existing"))
                        .replace("%prefix%", Utils.Chat((String) lang.get("Prefix")))
                        .replace("%id%", id)));

                return;

            }

            List<ItemStack> contents = new ArrayList<>();

            for (Object o : list) {

                if (o == null) {

                    contents.add(null);
                    continue;

                }

                ItemStack _i = (ItemStack) o;

                contents.add(_i);

            }

            ItemStack[] items = contents.toArray(new ItemStack[0]);

            inv.setContents(items);

            backpackStorage.put(id, inv);

            list.clear();
            contents.clear();

        }

        backPacks.clear();

    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {

        if (checkingBackpack.containsKey(e.getPlayer().getKiller())) {

            Player p = e.getPlayer().getKiller();
            String id = checkingBackpack.get(p);
            Inventory inv = e.getInventory();

            if (backpackStorage.containsKey(id)) {

                backpackStorage.replace(id, inv);

            } else {

                data.set(id + ".contents", inv.getContents());
                data.reloadConfig();

            }

            checkingBackpack.remove(e.getPlayer().getKiller());

        }


    }

}
