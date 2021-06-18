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
import uk.co.tmdavies.floosbackpacks.FloosBackpacks;
import uk.co.tmdavies.floosbackpacks.utils.Config;
import uk.co.tmdavies.floosbackpacks.utils.Utils;

import java.util.HashMap;
import java.util.UUID;

public class BackpackCommand implements CommandExecutor {

    private final FloosBackpacks plugin;
    private Config config;
    private Config lang;
    private Config data;
    private HashMap<UUID, Inventory> backpackStorage;

    public BackpackCommand(FloosBackpacks plugin) {

        this.plugin = plugin;
        this.config = plugin.config;
        this.lang = plugin.lang;
        this.data = plugin.data;
        this.backpackStorage = plugin.backpackStorage;

        plugin.getCommand("backpack").setExecutor(this);

    }

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {

        if (!sender.hasPermission("floosbackpack.admin")) return true;

        Player p = null;
        boolean isConsole = !(sender instanceof Player);

        if (!isConsole) p = (Player) sender;

        switch(args.length) {

            case 3:
                if (args[0].equalsIgnoreCase("give")) {

                    Player target = Bukkit.getPlayer(args[1]);
                    int size = Integer.parseInt(args[2]);

                    if (size == 0 || size > 54) {

                        sender.sendMessage(Utils.Chat(String.valueOf(lang.get("Misc.Invalid-Size")))
                                .replace("%prefix%", Utils.Chat(String.valueOf(lang.get("Prefix")))));

                    }

                    if (!target.isOnline() || target == null) {

                        sender.sendMessage(Utils.Chat(String.valueOf(lang.get("Misc.Target-Offline"))
                                .replace("%prefix%", Utils.Chat(String.valueOf(lang.get("Prefix"))))
                                .replace("%target%", args[1])));

                        return true;

                    }

                    ItemStack item = new ItemStack(Material.valueOf((String) config.get("Backpack.Material")), 1);

                    String uuid = UUID.randomUUID().toString();

                    NBTItem nbtItem = new NBTItem(item);

                    nbtItem.setString("id", uuid);
                    nbtItem.applyNBT(item);
                    item = nbtItem.getItem();

                    ItemMeta iMeta = item.getItemMeta();

                    item.getItemMeta().setDisplayName(Utils.Chat((String) config.get("Backpack.Name")));
                    item.setItemMeta(iMeta);

                    Inventory inv = Bukkit.createInventory(null, size, Utils.Chat((String) config.get("Backpack.Name")));

                    backpackStorage.put(UUID.fromString(uuid), inv);
                    target.getInventory().addItem(item);

                    target.sendMessage(Utils.Chat(String.valueOf(lang.get("Backpack.Give"))
                            .replace("%prefix%", Utils.Chat(String.valueOf(lang.get("Prefix"))))
                            .replace("%sender%", isConsole ? "CONSOLE" : p.getDisplayName())));

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
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat("&cUsage: /bp give {name} {size}"));
        Utils.sendPlayerCenteredMessage(sender, Utils.Chat(""));

    }

}
