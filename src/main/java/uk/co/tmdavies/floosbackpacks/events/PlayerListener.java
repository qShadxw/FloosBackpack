package uk.co.tmdavies.floosbackpacks.events;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
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

    public PlayerListener(FloosBackpacks plugin) {

        this.config = plugin.config;
        this.data = plugin.data;
        this.lang = plugin.lang;
        this.backpackStorage = plugin.backpackStorage;
        this.checkingBackpack = plugin.checkingBackpack;

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) return;

        Player p = e.getPlayer();

        ItemStack hand = p.getInventory().getItemInMainHand();

        if (hand == null || hand.getType().equals(Material.AIR)) return;
        if (!(hand.getType() == Material.valueOf((String) config.get("Backpack.Material")))) return;
        if (!hand.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.Chat((String) config.get("Backpack.Name")))) return;

        NBTItem nbtItem = new NBTItem(hand);

        if (nbtItem.getString("id").equals("")) return;

        e.setCancelled(true);

        String uuid = nbtItem.getString("id");

        Inventory inv = null;

        if (!backpackStorage.containsKey(uuid)) {

            // Data.yml Check + Result Load
            for (int i = 1; i < 101; i++) {

                List<?> list = data.getConfig().getList(uuid + ".contents");

                if (list == null) {

                    p.sendMessage(Utils.Chat(String.valueOf(lang.get("Backpack.Not-Existing"))
                            .replace("%prefix%", Utils.Chat((String) lang.get("Prefix")))
                            .replace("%id%", uuid.equals("") ? "No-ID" : uuid)));

                    return;

                }

                inv = Bukkit.createInventory(null, data.getConfig().getInt(uuid + ".size"),
                        Utils.Chat(config.getConfig().getString("Backpack.Name")));

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

                backpackStorage.put(uuid, inv);

            }

        } else {

            inv = backpackStorage.get(uuid);

        }

        p.openInventory(inv);

    }

    @EventHandler
    public void onInteractOffHand(PlayerInteractEvent e) {

        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) return;

        Player p = e.getPlayer();

        ItemStack hand = p.getInventory().getItemInOffHand();

        if (hand == null || hand.getType().equals(Material.AIR)) return;
        if (!(hand.getType() == Material.valueOf((String) config.get("Backpack.Material")))) return;
        if (!hand.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.Chat((String) config.get("Backpack.Name")))) return;

        NBTItem nbtItem = new NBTItem(hand);

        if (nbtItem.getString("id").equals("")) return;

        e.setCancelled(true);

        String uuid = nbtItem.getString("id");

        Inventory inv = null;

        if (!backpackStorage.containsKey(uuid)) {

            // Data.yml Check + Result Load
            for (int i = 1; i < 101; i++) {

                List<?> list = data.getConfig().getList(uuid + ".contents");

                if (list == null) {

                    p.sendMessage(Utils.Chat(String.valueOf(lang.get("Backpack.Not-Existing"))
                            .replace("%prefix%", Utils.Chat((String) lang.get("Prefix")))
                            .replace("%id%", uuid.equals("") ? "No-ID" : uuid)));

                    return;

                }

                inv = Bukkit.createInventory(null, data.getConfig().getInt(uuid + ".size"),
                        Utils.Chat(config.getConfig().getString("Backpack.Name")));

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

                backpackStorage.put(uuid, inv);

            }

        } else {

            inv = backpackStorage.get(uuid);

        }

        p.openInventory(inv);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player p = e.getPlayer();

        HashMap<ItemStack, String> backPacks = new HashMap<>();
        List<String> playerCache = new ArrayList<>();

        // Inventory Check.
        for (ItemStack item : p.getInventory()) {

            if (item == null) continue;

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.getString("id").equals("")) backPacks.put(item, nbtItem.getString("id"));

        }

        // Ender Chest Check.
        for (ItemStack item : p.getEnderChest()) {

            if (item == null) continue;

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.getString("id").equals("")) backPacks.put(item, nbtItem.getString("id"));

        }

        // Save IDs saved from previous checks.
        for (ItemStack item : backPacks.keySet()) {

            NBTItem nbtItem = new NBTItem(item);
            String id = nbtItem.getString("id");
            Inventory inv = backpackStorage.get(id);

            data.set(id + ".size", inv.getSize());
            data.set(id + ".contents", inv.getContents());
            data.saveConfig();

            playerCache.add(id);

            backpackStorage.remove(id);

        }

        List<String> ids = new ArrayList<>();

        for (String s : backpackStorage.keySet()) {

            String[] split = s.split("-");

            if (split[0].equalsIgnoreCase(p.getName())) {

                ids.add(s);

            }

        }

        for (String id : ids) {

            if (playerCache.contains(id)) continue;

            Inventory inv = backpackStorage.get(id);

            data.set(id + ".size", inv.getSize());
            data.set(id + ".contents", inv.getContents());
            data.saveConfig();

            backpackStorage.remove(id);

            playerCache.remove(id);

        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        HashMap<ItemStack, String> backPacks = new HashMap<>();

        // Inventory Check
        for (ItemStack item : p.getInventory()) {

            if (item == null) continue;

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.getString("id").equals("")) backPacks.put(item, nbtItem.getString("id"));

        }

        // Ender Chest Check
        for (ItemStack item : p.getEnderChest()) {

            if (item == null) continue;

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.getString("id").equals("")) backPacks.put(item, nbtItem.getString("id"));

        }

        // Inventory Check Result Load
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

        // Data.yml Check + Result Load
        for (int i = 1; i < 101; i++) {

            boolean isLoaded = false;

            String id = p.getName() + "-" + i;

            for (String string : backPacks.values()) {

                if (id.equals(string)) {

                    isLoaded = true;

                    break;

                }

            }

            if (isLoaded) continue;

            Inventory inv = Bukkit.createInventory(null, data.getConfig().getInt(id + ".size"),
                    Utils.Chat(config.getConfig().getString("Backpack.Name")));

            List<?> list = data.getConfig().getList(id + ".contents");

            if (list == null) return;

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

        }

        backPacks.clear();

    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {

        // Through ./bp check command.
        if (checkingBackpack.containsKey(e.getPlayer().getKiller())) {

            Player p = (Player) e.getPlayer();
            String id = checkingBackpack.get(p);
            Inventory inv = e.getInventory();

            if (backpackStorage.containsKey(id)) {

                backpackStorage.replace(id, inv);

            } else {

                data.set(id + ".contents", inv.getContents());
                data.saveConfig();

            }

            checkingBackpack.remove(e.getPlayer().getKiller());

        }

        // If Player has another person's bp.
        if (e.getInventory().getTitle().equals(Utils.Chat((String) config.get("Backpack.Name")))) {

            Player p = (Player) e.getPlayer();
            String id;

            ItemStack item = p.getInventory().getItemInMainHand();

            NBTItem nbtItem = new NBTItem(item);

            if (nbtItem.getString("id").equals("")) {

                item = p.getInventory().getItemInOffHand();
                nbtItem = new NBTItem(item);

            }

            if (nbtItem.getString("id").equals("")) return;

            id = nbtItem.getString("id");

            Inventory invToSave = e.getInventory();

            backpackStorage.replace(id, invToSave);

            data.set(id + ".contents", invToSave.getContents());
            data.saveConfig();

        }


    }

    @EventHandler
    public void onItemClickInGUI(InventoryClickEvent e) {

        // Checks if clicker is Player.
        if (!(e.getWhoClicked() instanceof Player)) return;

        // Checks Backpack's Name equals Backpack Name.
        if (!e.getInventory().getTitle().equals(Utils.Chat((String) config.get("Backpack.Name")))) return;

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        // Checks if Item clicked is a Backpack Material.
        if (item.getType() != Material.valueOf((String) config.get("Backpack.Material"))) return;

        NBTItem nbtItem = new NBTItem(item);

        // Checks if Item clicked has an ID.
        if (nbtItem.getString("id").equals("")) return;

        // Cancels Event.
        e.setCancelled(true);

        p.updateInventory();

    }

}
