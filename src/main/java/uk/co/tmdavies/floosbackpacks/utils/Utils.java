package uk.co.tmdavies.floosbackpacks.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.apache.commons.codec.binary.Base64;
import uk.co.tmdavies.floosbackpacks.FloosBackpacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Utils {

    private static Config data;
    private static HashMap<String, Inventory> backpackStorage;

    private final static int CENTER_PX = 154;

    public Utils(FloosBackpacks plugin) {

        data = plugin.data;
        backpackStorage = plugin.backpackStorage;

    }

    public static String Chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendCenteredMessage(String message) {
        if (message == null || message.equals(""))
            Bukkit.broadcastMessage("");
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode == true) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else
                    isBold = false;
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        Bukkit.broadcastMessage(sb.toString() + message);
    }

    public static void sendPlayerCenteredMessage(CommandSender p, String message) {
        if (message == null || message.equals(""))
            p.sendMessage("");
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else
                    isBold = false;
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        p.sendMessage(sb.toString() + message);
    }

    public enum DefaultFontInfo {

        A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5), E('E', 5), e('e', 5),
        F('F', 5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1), J('J', 5), j('j', 5),
        K('K', 5), k('k', 4), L('L', 5), l('l', 1), M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5),
        P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5), S('S', 5), s('s', 5), T('T', 5), t('t', 4),
        U('U', 5), u('u', 5), V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5), Y('Y', 5), y('y', 5),
        Z('Z', 5), z('z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5), NUM_5('5', 5), NUM_6('6', 5),
        NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0('0', 5), EXCLAMATION_POINT('!', 1), AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5), DOLLAR_SIGN('$', 5), PERCENT('%', 5), UP_ARROW('^', 5), AMPERSAND('&', 5), ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4), RIGHT_PERENTHESIS(')', 4), MINUS('-', 5), UNDERSCORE('_', 5), PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5), LEFT_CURL_BRACE('{', 4), RIGHT_CURL_BRACE('}', 4), LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3), COLON(':', 1), SEMI_COLON(';', 1), DOUBLE_QUOTE('"', 3), SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4), RIGHT_ARROW('>', 4), QUESTION_MARK('?', 5), SLASH('/', 5), BACK_SLASH('\\', 5),
        LINE('|', 1), TILDE('~', 5), TICK('`', 2), PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3), DEFAULT('a', 4);

        private char character;
        private int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == DefaultFontInfo.SPACE)
                return this.getLength();
            return this.length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c)
                    return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }
    }

    public static ItemStack createSkullUrl(String name, String url, List<String> lore) {

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        PropertyMap propertyMap = profile.getProperties();

        if (propertyMap == null) {

            throw new IllegalStateException("Profile doesn't contain a property map");

        }

        Base64 base64 = new Base64();

        byte[] encodedData = base64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        propertyMap.put("textures", new Property("textures", new String(encodedData)));

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        ItemMeta headMeta = head.getItemMeta();

        Class<?> headMetaClass = headMeta.getClass();

        Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);

        headMeta.setDisplayName(name);

        if (lore != null) {

            lore.forEach(Utils::Chat);
            headMeta.setLore(lore);

        }

        head.setItemMeta(headMeta);

        return head;
    }

    public static void saveBackpacks(Player p) {

        List<ItemStack> backPacks = new ArrayList<>();

        for (ItemStack item : p.getInventory()) {

            if (item == null) continue;

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.getString("id").equals("")) backPacks.add(item);

        }

        for (ItemStack item : backPacks) {

            NBTItem nbtItem = new NBTItem(item);
            String id = nbtItem.getString("id");
            Inventory inv = backpackStorage.get(id);

            data.set(id + ".size", inv.getSize());
            data.set(id + ".contents", inv.getContents());
            data.saveConfig();

            backpackStorage.remove(id);

        }

    }


}
