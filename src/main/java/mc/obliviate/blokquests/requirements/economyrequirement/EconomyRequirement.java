package mc.obliviate.blokquests.requirements.economyrequirement;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class EconomyRequirement implements QuestRequirement {

	private final double money;

	public EconomyRequirement(double money) {
		this.money = money;
	}

	public static EconomyRequirement deserialize(ConfigurationSection section) {
		return new EconomyRequirement(section.getDouble("money"));
	}

	@Override
	public boolean canMeet(Player player) {
		return BlokQuests.getEconomy().has(player, money);
	}

	@Override
	public void removeRequirements(Player player) {
		BlokQuests.getEconomy().withdrawPlayer(player, money);
	}
}
