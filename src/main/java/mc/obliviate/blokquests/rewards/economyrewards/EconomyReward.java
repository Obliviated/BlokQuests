package mc.obliviate.blokquests.rewards.economyrewards;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.rewards.QuestReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class EconomyReward implements QuestReward {

	private final double money;

	private EconomyReward(double money) {
		this.money = money;
	}

	@Override
	public void deliverRewards(Player player) {
		BlokQuests.getEconomy().depositPlayer(player,money);
	}

	public static EconomyReward deserialize(ConfigurationSection section) {
		return new EconomyReward(section.getDouble("money"));
	}
}
