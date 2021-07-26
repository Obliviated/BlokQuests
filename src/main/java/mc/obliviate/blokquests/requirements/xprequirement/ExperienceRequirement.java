package mc.obliviate.blokquests.requirements.xprequirement;

import mc.obliviate.blokquests.requirements.QuestRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ExperienceRequirement implements QuestRequirement {


	private final int experience;

	private ExperienceRequirement(int experience) {
		this.experience = experience;
	}

	public static ExperienceRequirement deserialize(ConfigurationSection section) {
		return new ExperienceRequirement(section.getInt("experience"));
	}

	@Override
	public boolean canMeet(Player player) {
		return player.getTotalExperience() >= experience;
	}

	@Override
	public void removeRequirements(Player player) {

	}
}
