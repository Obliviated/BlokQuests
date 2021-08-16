package mc.obliviate.blokquests.requirements.bentoboxrequirement;

import mc.obliviate.blokquests.handlers.PlaceholderAPIHandler;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class BentoSkyLevelRequirement implements QuestRequirement {

	final int level;

	private BentoSkyLevelRequirement(final int level) {
		this.level = level;
	}

	public static BentoSkyLevelRequirement deserialize(ConfigurationSection section) {
		final int level = section.getInt("island-level");
		return new BentoSkyLevelRequirement(level);
	}


	@Override
	public boolean canMeet(final Player player) {

		int level;
		final String levelString = PlaceholderAPIHandler.parsePlaceholders(player, "Level_aoneblock_island_level");
		try {
			level = Integer.parseInt(levelString);
		}catch (NumberFormatException e) {
			Bukkit.getLogger().severe("AOneBlock island level could not parsed as a string: " + levelString);
			return false;
		}

		return level >= this.level;

	}




	@Override
	public void removeRequirements(Player player) {

	}
}
