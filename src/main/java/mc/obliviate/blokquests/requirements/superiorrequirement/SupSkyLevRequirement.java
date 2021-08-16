package mc.obliviate.blokquests.requirements.superiorrequirement;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SupSkyLevRequirement implements QuestRequirement {

	private final int level;

	private SupSkyLevRequirement(final int level) {
		this.level = level;
	}

	public static SupSkyLevRequirement deserialize(ConfigurationSection section) {
		final int level = section.getInt("island-level");
		return new SupSkyLevRequirement(level);
	}

	@Override
	public boolean canMeet(Player player) {
		Island island = SuperiorSkyblockAPI.getPlayer(player).getIsland();
		if (island == null) return false;
		return island.getIslandLevel().intValue() >= level;
	}

	@Override
	public void removeRequirements(Player player) {
	}
}
