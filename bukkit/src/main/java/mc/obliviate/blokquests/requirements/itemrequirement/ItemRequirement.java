package mc.obliviate.blokquests.requirements.itemrequirement;

import mc.obliviate.blokquests.requirements.QuestRequirement;
import mc.obliviate.blokquests.utils.ConfigItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemRequirement implements QuestRequirement {

    private final List<ConfigItem> itemStackList;

    private ItemRequirement(List<ConfigItem> itemStackList) {
        this.itemStackList = itemStackList;
    }

    public static ItemRequirement deserialize(ConfigurationSection section) {
        final List<ConfigItem> itemStackList = new ArrayList<>();
        int lastIndex = 0;
        while (section.getString(++lastIndex + "") != null) {
            itemStackList.add(ConfigItem.deserializeConfigurableItem(section.getConfigurationSection(lastIndex + "")));
        }
        return new ItemRequirement(itemStackList);
    }

    private static int getReqItemAmount(final Player player, final ConfigItem rItem) {
        int total = 0;
        for (int i = 0; i < 36; i++) {
            final ItemStack item = player.getInventory().getContents()[i];
            if (item == null || item.getType().equals(Material.AIR)) continue;
            if (rItem.compare(item, true)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    private static void removeReqItem(final Player player, final ConfigItem rItem) {
        int need = rItem.getAmount();
        for (int i = 0; i < 36; i++) {
            final ItemStack item = player.getInventory().getContents()[i];
            if (item == null || item.getType().equals(Material.AIR)) continue;
            if (rItem.compare(item, true)) {
                if (need >= item.getAmount()) {
                    need -= item.getAmount();
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - need);
                    player.getInventory().setItem(i, item);
                    break;
                }
            }
        }

    }

    @Override
    public boolean canMeet(final Player player) {
        for (ConfigItem rItem : itemStackList) {
            int amount = getReqItemAmount(player, rItem);
            if (amount < rItem.getAmount()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void removeRequirements(final Player player) {
        for (ConfigItem rItem : itemStackList) {
            removeReqItem(player, rItem);
        }
    }

    public List<ConfigItem> getItemStackList() {
        return itemStackList;
    }
}
