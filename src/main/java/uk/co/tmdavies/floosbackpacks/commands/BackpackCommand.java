package uk.co.tmdavies.floosbackpacks.commands;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import uk.co.tmdavies.floosbackpacks.FloosBackpacks;
import uk.co.tmdavies.floosbackpacks.utils.Config;
import uk.co.tmdavies.floosbackpacks.utils.SkullCreator;
import uk.co.tmdavies.floosbackpacks.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BackpackCommand implements CommandExecutor {

    private final FloosBackpacks plugin;
    private Config config;
    private Config lang;
    private Config data;
    private HashMap<String, Inventory> backpackStorage;
    private HashMap<Player, String> checkingBackpack;

    public BackpackCommand(FloosBackpacks plugin) {

        this.plugin = plugin;
        this.config = plugin.config;
        this.lang = plugin.lang;
        this.data = plugin.data;
        this.backpackStorage = plugin.backpackStorage;
        this.checkingBackpack = plugin.checkingBackpack;

        plugin.getCommand("backpack").setExecutor(this);

    }

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {

        Player p = null;
        boolean isConsole = !(sender instanceof Player);

        if (!isConsole) p = (Player) sender;

        switch(args.length) {

            case 3:
                if (args[0].equalsIgnoreCase("give")) {

                    if (!sender.hasPermission("floosbackpacks.give")) {

                        sender.sendMessage(Utils.Chat(String.valueOf(lang.get("Misc.No-Permission"))
                                .replace("%prefix%", Utils.Chat((String) lang.get("Prefix")))
                                .replace("%permission%", "floosbackpacks.give")));

                        return true;

                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    int size = Integer.parseInt(args[2]);

                    if (size == 0 || size > 54) {

                        sender.sendMessage(Utils.Chat(String.valueOf(lang.get("Misc.Invalid-Size")))
                                .replace("%prefix%", Utils.Chat(String.valueOf(lang.get("Prefix")))));

                        return true;

                    }

                    if (!target.isOnline() || target == null) {

                        sender.sendMessage(Utils.Chat(String.valueOf(lang.get("Misc.Target-Offline"))
                                .replace("%prefix%", Utils.Chat(String.valueOf(lang.get("Prefix"))))
                                .replace("%target%", args[1])));

                        return true;

                    }

                    int i = 1;

                    for (String id : backpackStorage.keySet()) {

                        String[] temp = id.split("-");

                        if (temp[0].equalsIgnoreCase(target.getName())) i++;

                    }

                    String id = target.getName() + "-" + i;
                    ItemStack item = new ItemStack(Material.valueOf((String) config.get("Backpack.Material")), 1);

                    List<String> lore = new ArrayList<>();

                    lore.add(Utils.Chat("&1"));
                    lore.add(Utils.Chat("&8&oBackpack ID: " + id));

                    if (item.getType() == Material.SKULL_ITEM)
                        item = SkullCreator.itemFromBase64((String) config.get("Backpack.SkinUrl"));

                    ItemMeta iMeta = item.getItemMeta();

                    iMeta.setDisplayName(Utils.Chat((String) config.get("Backpack.Name")));
                    iMeta.setLore(lore);
                    item.setItemMeta(iMeta);

                    NBTItem nbtItem = new NBTItem(item);

                    nbtItem.setString("id", id);
                    nbtItem.applyNBT(item);

                    item = nbtItem.getItem();

                    Inventory inv = Bukkit.createInventory(null, size, Utils.Chat((String) config.get("Backpack.Name")));

                    backpackStorage.put(id, inv);
                    target.getInventory().addItem(item);

                    target.sendMessage(Utils.Chat(String.valueOf(lang.get("Backpack.Give"))
                            .replace("%prefix%", Utils.Chat(String.valueOf(lang.get("Prefix"))))
                            .replace("%sender%", isConsole ? "CONSOLE" : p.getDisplayName())));

                }

                if (args[0].equalsIgnoreCase("check")) {

                    if (isConsole) {

                        sender.sendMessage(Utils.Chat("&cOnly players may execute this command."));
                        return true;

                    }

                    if (!sender.hasPermission("floosbackpacks.check")) {

                        sender.sendMessage(Utils.Chat(String.valueOf(lang.get("Misc.No-Permission"))
                                .replace("%prefix%", Utils.Chat((String) lang.get("Prefix")))
                                .replace("%permission%", "floosbackpacks.check")));

                        return true;

                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    int backpackNo = Integer.parseInt(args[2]);

                    if (!target.isOnline() || target == null) {

                        sender.sendMessage(Utils.Chat(String.valueOf(lang.get("Misc.Target-Offline"))
                                .replace("%prefix%", Utils.Chat(String.valueOf(lang.get("Prefix"))))
                                .replace("%target%", args[1])));

                        return true;

                    }

                    String id = target.getName() + "-" + backpackNo;

                    Inventory inv = Bukkit.createInventory(null, data.getConfig().getInt(id + ".size"),
                            Utils.Chat(config.getConfig().getString("Backpack.Name")));

                    if (backpackStorage.containsKey(id)) {

                        inv = backpackStorage.get(id);

                    } else {

                        List<?> list = data.getConfig().getList(id + ".contents");

                        if (list == null) {

                            p.sendMessage(Utils.Chat(String.valueOf(lang.get("Backpack.Not-Existing"))
                                    .replace("%prefix%", Utils.Chat((String) lang.get("Prefix")))
                                    .replace("%id%", id)));

                            return true;

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

                        list.clear();
                        contents.clear();

                    }

                    p.openInventory(inv);

                    checkingBackpack.put(p, id);

                }
                break;

            default:
                invalidArgs(sender);
                break;

        }

        return true;

    }

    public void invalidArgs(CommandSender sender) {

        Utils.sendPlayerCenteredMessage(sender, Utils.Chat(""));
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat("&6&lFloosBackpacks"));
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat("&8&oby Carbonate"));
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat(""));
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat("&cUsages:"));
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat("&c/bp give {name} {size}"));
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat("&c/bp check {name} {number}"));
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat(""));

    }

}
