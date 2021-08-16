package mc.obliviate.blokquests.handlers;

import mc.obliviate.blokquests.BlokQuests;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderAPIHandler {

	private static boolean isDisabled;

	public static String parsePlaceholders(final Player player, final String text) {
		if (isDisabled()) return text;
		return PlaceholderAPI.setPlaceholders(player, text);
	}

	public static boolean isDisabled() {
		return isDisabled;
	}

	public void init(BlokQuests plugin) {
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			plugin.getLogger().info("PlaceholderAPI plugin found. The API successfully hooked.");
			isDisabled = false;
			return;
		}
		isDisabled = true;
	}
}
