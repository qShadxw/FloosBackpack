package uk.co.tmdavies.floosbackpacks.guis;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.components.xseries.XMaterial;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.tmdavies.floosbackpacks.FloosBackpacks;
import uk.co.tmdavies.floosbackpacks.utils.SkullCreator;
import uk.co.tmdavies.floosbackpacks.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BackPackGUI {

    PaginatedGui gui;
    final FloosBackpacks plugin = JavaPlugin.getPlugin(FloosBackpacks.class);

    public BackPackGUI() {
        gui = new PaginatedGui(6, "BackPacks");
    }

    public BackPackGUI setupInventory(Player target) {

        ItemStack filler = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
        ItemMeta fMeta = filler.getItemMeta();

        fMeta.setDisplayName(" ");
        filler.setItemMeta(fMeta);

        gui.setItem(49, ItemBuilder.from(XMaterial.BARRIER.parseItem()).setName("Close").asGuiItem(event -> {
            event.setCancelled(true);
            gui.close(event.getWhoClicked());
        }));

        for (int i = 0; i < 10; i++) {
            if (i == 4) continue;
            if (gui.getGuiItem(i) == null) {
                gui.setItem(i, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
            }
        }

        gui.setItem(17, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(18, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(26, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(27, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(35, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(36, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(48, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(50, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));

        for (int i = 44; i < 54; i++) {
            if (gui.getGuiItem(i) == null) {
                gui.setItem(i, ItemBuilder.from(filler).asGuiItem(event -> event.setCancelled(true)));
            }
        }


        ItemStack icon = SkullCreator.itemFromUuid(target.getUniqueId());
        ItemMeta iMeta = icon.getItemMeta();

        iMeta.setDisplayName(Utils.Chat("&a" + target.getDisplayName()));

        icon.setItemMeta(iMeta);

        gui.setItem(4, ItemBuilder.from(icon).asGuiItem(event -> event.setCancelled(true)));

        // start at 29
        List<String> ids = new ArrayList<>();

        for (int i = 1; i < 101; i++) {

            if (plugin.backpackStorage.containsKey(target.getName() + "-" + i)) {

                ids.add(target.getName() + "-" + i);

            }

        }

        for (String id : ids) {

            ItemStack item = new ItemStack(Material.valueOf((String) plugin.config.get("Backpack.Material")), 1);

            List<String> lore = new ArrayList<>();

            lore.add(Utils.Chat("&1"));
            lore.add(Utils.Chat("&8&oBackpack ID: " + id));

            if (item.getType() == Material.SKULL_ITEM)
                item = SkullCreator.itemFromBase64((String) plugin.config.get("Backpack.SkinUrl"));

            ItemMeta sMeta = item.getItemMeta();

            sMeta.setDisplayName(Utils.Chat((String) plugin.config.get("Backpack.Name")));
            sMeta.setLore(lore);
            item.setItemMeta(sMeta);

            gui.addItem(ItemBuilder.from(item).asGuiItem(event -> {

                event.setCancelled(true);

                Player p = (Player) event.getWhoClicked();

                p.closeInventory();
                p.openInventory(plugin.backpackStorage.get(id));

            }));


        }

        return this;
    }

    public BackPackGUI openInventory(Player p) {
        gui.open(p);
        return this;
    }
}
