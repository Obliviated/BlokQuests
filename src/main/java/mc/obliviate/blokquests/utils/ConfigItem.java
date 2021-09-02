package mc.obliviate.blokquests.utils;

import mc.obliviate.blokquests.utils.internalplaceholder.PlaceholderUtil;
import mc.obliviate.blokquests.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigItem {

	private final int amount;
	private final MaterialData material;
	private final String name;
	private final List<String> lore;
	private final Map<Enchantment, Integer> enchantments;

	private ConfigItem(int amount, MaterialData material, String name, List<String> lore, Map<Enchantment, Integer> enchantments) {
		this.amount = amount;
		this.material = material;
		this.name = name;
		this.lore = lore;
		this.enchantments = enchantments;
	}

	public static ConfigItem deserializeConfigurableItem(ConfigurationSection section) {
		final int amount = section.getInt("amount", 1);

		final Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(section.getString("material-type") + ":" + section.getInt("material-data", 0));
		if (!xMaterial.isPresent()) {
			Bukkit.getLogger().severe("Item Material is null: " + section.getString("material-type") + " in " + section.getRoot().getName());
			return new ConfigItem(0, new MaterialData(Material.STONE, (byte) 0), null, null, null);
		}

		String name = null;
		List<String> lore = null;
		Map<Enchantment, Integer> enchantments = null;
		if (!section.getStringList("enchantments").isEmpty()) {
			enchantments = new HashMap<>();
			for (final String enchantmentString : section.getStringList("enchantments")) {
				final String[] enchantmentData = enchantmentString.split("=");

				final Enchantment enchantment = BlokUtils.parseEnchantOrError(enchantmentData[0], "");
				final int enchantmentValue = BlokUtils.parseIntOrError(enchantmentData[1], "invalid enchantment value (" + enchantmentData[0] + ")");
				enchantments.put(enchantment, enchantmentValue);
			}
		} else if (section.getBoolean("glow")) {
			enchantments = new HashMap<>();
			enchantments.put(Enchantment.DURABILITY, 0);
		}

		if (section.getString("name") != null) {
			name = section.getString("name");
		}
		lore = section.getStringList("lore");

		return new ConfigItem(amount, new MaterialData(xMaterial.get().parseMaterial(), xMaterial.get().getData()), BlokUtils.parseColor(name), BlokUtils.parseColor(lore), enchantments);

	}

	public boolean compare(final ItemStack item, boolean ignoreAmount) {
		if (!ignoreAmount && !(amount == 0 || item.getAmount() >= amount)) return false;

		if (!(material == null || material.equals(item.getData()))) return false;

		if (!(name == null || name.isEmpty() || name.equalsIgnoreCase(item.getItemMeta().getDisplayName())))
			return false;

		if (!(lore == null || lore.isEmpty() || lore.equals(item.getItemMeta().getLore()))) return false;

		if (!(enchantments == null || enchantments.isEmpty() || enchantments.equals(item.getEnchantments())))
			return false;

		return true;
	}

	public ItemStack toItemStack() {
		return toItemStack(new PlaceholderUtil());
	}

	public ItemStack toItemStack(PlaceholderUtil placeholder) {

		final ItemStack result = material.toItemStack(amount);


		if (name != null || lore != null) {
			final ItemMeta meta = result.getItemMeta();
			if (name != null) {
				meta.setDisplayName(placeholder.apply(name));
			}
			if (lore != null) {
				meta.setLore(placeholder.apply(lore));
			}
			result.setItemMeta(meta);
		}

		if (enchantments != null) {
			if (material.getItemType().equals(Material.ENCHANTED_BOOK)) {
				final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
				if (meta == null) return result;
				for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
					meta.addStoredEnchant(enchant.getKey(), enchant.getValue(), true);
				}
				result.setItemMeta(meta);
			} else {
				result.addUnsafeEnchantments(enchantments);
			}
		}

		return result;
	}

	public int getAmount() {
		return amount;
	}

	public MaterialData getMaterial() {
		return material;
	}

	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	@Override
	public String toString() {
		return "QuestItem{" +
				"amount=" + amount +
				", material=" + material +
				", name='" + name + '\'' +
				", lore=" + lore +
				", enchantments=" + enchantments +
				'}';
	}
}
