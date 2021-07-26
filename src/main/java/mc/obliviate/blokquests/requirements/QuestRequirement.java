package mc.obliviate.blokquests.requirements;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface QuestRequirement {

	boolean canMeet(Player player);
	void removeRequirements(Player player);

	static QuestRequirement deserializeRequirement(ConfigurationSection section) {
		return null;
	}
}
