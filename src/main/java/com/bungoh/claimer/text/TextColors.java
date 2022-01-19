package com.bungoh.claimer.text;

import com.bungoh.claimer.Claimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextColors {

    private static TextColors instance;
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#(\\d|[A-F]|[a-f]){6})");
    private final boolean hasHexSupport;

    private TextColors(boolean hasHexSupport) {
        this.hasHexSupport = hasHexSupport;
    }

    public static String translate(String text) {
        if (text == null) return "null";
        if (hasHexSupport() && HEX_PATTERN.matcher(text).find()) {
            final Matcher matcher = HEX_PATTERN.matcher(text);
            final StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, ChatColor.valueOf(matcher.group(1)).toString());
            }
            matcher.appendTail(sb);
            text = sb.toString();
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    //Checks if version is 1.16+
    public static boolean hasHexSupport() {
        return instance != null ? instance.hasHexSupport : Bukkit.getVersion().matches("1\\.(1[6-9]|[2-9][0-9]).*");
    }

    public static void setup(Claimer plugin) {
        instance = new TextColors(hasHexSupport());
        if (instance.hasHexSupport) {
            plugin.getLogger().info("Loaded 1.16 and up hex support!");
        }
    }

}
