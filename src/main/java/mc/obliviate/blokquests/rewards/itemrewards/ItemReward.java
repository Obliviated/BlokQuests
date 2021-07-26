package mc.obliviate.blokquests.rewards.itemrewards;

import mc.obliviate.blokquests.utils.ConfigItem;
import mc.obliviate.blokquests.rewards.QuestReward;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemReward implements QuestReward {

	private final List<ItemStack> items;

	private ItemReward(List<ItemStack> items) {
		this.items = items;
	}

	@Override
	public void deliverRewards(Player player) {
		for (ItemStack item : items) {
			player.getInventory().addItem(item);
		}
	}

	public static ItemReward deserialize(ConfigurationSection section) {
		final List<ItemStack> itemStackList = new ArrayList<>();
		int lastIndex = 0;
		while (section.getString(++lastIndex + "") != null) {
			Bukkit.getLogger().info("item rew-" + lastIndex);
			itemStackList.add(ConfigItem.deserializeConfigurableItem(section.getConfigurationSection(lastIndex + "")).toItemStack());
		}
		return new ItemReward(itemStackList);
	}


}
