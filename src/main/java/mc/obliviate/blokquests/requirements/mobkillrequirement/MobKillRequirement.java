package mc.obliviate.blokquests.requirements.mobkillrequirement;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MobKillRequirement implements QuestRequirement {

	private final Map<EntityType, Integer> mobKills;

	private MobKillRequirement(Map<EntityType, Integer> mobKills) {
		this.mobKills = mobKills;
	}

	public Set<EntityType> getEntityTypes() {
		return mobKills.keySet();
	}

	public static int getMobKills(Player player, EntityType entityType) {
		return BlokQuests.getaDatabase().getIntegerData(player,"mob-kills." + entityType);
	}
	public static void addKillMobs(Player player, EntityType entityType, int amount) {
		BlokQuests.getaDatabase().addData(player, "mob-kills." + entityType, getMobKills(player, entityType) + amount);
	}

	public static MobKillRequirement deserialize(ConfigurationSection section) {
		final Map<EntityType, Integer> result = new HashMap<>();
		for (String mob : section.getKeys(false)) {
			EntityType type;
			try {
				type = EntityType.valueOf(mob);
			} catch (EnumConstantNotPresentException | IllegalArgumentException e) {
				Bukkit.getLogger().severe("[BlokQuests] Entity Could not found: " + mob);
				continue;
			}
			result.put(type, section.getInt(mob));
		}
		return new MobKillRequirement(result);
	}


	@Override
	public boolean canMeet(Player player) {
		for (Map.Entry<EntityType, Integer> entry : mobKills.entrySet()) {

			int amount = getMobKills(player, entry.getKey());
			if (amount < entry.getValue()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void removeRequirements(Player player) {

	}
}
