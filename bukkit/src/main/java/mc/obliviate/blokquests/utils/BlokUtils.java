package mc.obliviate.blokquests.utils;

import mc.obliviate.blokquests.handlers.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlokUtils {

    public static int parseIntOrError(String integer2parse, String logNote) {
        try {
            return Integer.parseInt(integer2parse);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().severe(logNote + " - Could not parse as number: " + integer2parse);
        }
        return 0;
    }

    public static Enchantment parseEnchantOrError(String enchant2parse, String logNote) {
        try {
            final Enchantment enchantment = Enchantment.getByName(enchant2parse);
            if (enchantment == null) {
                throw new IllegalArgumentException();
            } else {
                return enchantment;
            }
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe(logNote + " - Could not parse as enchantment: " + enchant2parse);
        }
        return null;
    }

    public static String parseColor(String msg) {
        if (msg == null) return null;
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> parseColor(List<String> msg) {
        if (msg == null) return null;
        final List<String> result = new ArrayList<>();
        for (String line : msg) {
            result.add(parseColor(line));
        }
        return result;
    }

    public static String getDate() {
        return (new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss")).format(new Date());
    }

    public static void log(String text) {
        try {
            Writer writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ConfigHandler.getLogFile(), true), StandardCharsets.UTF_8)));

            writer.write("[" + getDate() + "] " + text + "\n");
            writer.flush();
            writer.close();
        } catch (IOException eb) {

            eb.printStackTrace();
        }
    }
}
