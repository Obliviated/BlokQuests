package mc.obliviate.blokquests.handlers.database;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.quest.Quest;
import mc.obliviate.blokquests.quest.QuestPage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlDatabase extends ADatabase {

	public YamlDatabase(BlokQuests plugin) {
		super(plugin);
	}

	private File getPlayerDataFile(Player player) {
		//                                                                                 i think uuid is unnecessary
		return new File(getPlugin().getDataFolder() + File.separator + "playerdata" + File.separator + player.getName().toLowerCase() + ".yml");
	}

	private YamlConfiguration getPlayerData(Player player) {
		YamlConfiguration data = getPlugin().getDataHandler().getDatas().get(player.getUniqueId());

		if (data != null) return data;

		data = YamlConfiguration.loadConfiguration(getPlayerDataFile(player));
		getPlugin().getDataHandler().getDatas().put(player.getUniqueId(), data);

		return data;
	}

	private void updatePlayerDataFile(Player player, YamlConfiguration data) {
		try {
			data.save(getPlayerDataFile(player));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCompletedParts(Quest quest, Player player) {
		if (quest == null) return 0;
		return getPlayerData(player).getInt("completed-quests." + quest.getName());
	}

	@Override
	public int getCompletedPages(Player player) {
		return getPlayerData(player).getInt("current-page", 0);
	}

	@Override
	public boolean isPageCompleted(QuestPage page, Player player) {
		return getCompletedPages(player) >= page.getPageNumber();
	}


	@Override
	public List<Quest> getCompletedQuests(Player player) {
		final List<Quest> result = new ArrayList<>();
		final ConfigurationSection data = getPlayerData(player).getConfigurationSection("completed-quests");
		for (String key : data.getKeys(false)) {
			final Quest quest = getPlugin().getDataHandler().findQuest(key);
			if (isCompleted(quest, player)) {
				result.add(quest);
			}
		}
		return result;
	}

	@Override
	public void addData(Player player, String key, Object value) {
		final YamlConfiguration data = getPlayerData(player);
		data.set(key, value);
		updatePlayerDataFile(player, data);
	}

	@Override
	public int getIntegerData(Player player, String key) {
		return getPlayerData(player).getInt(key, 0);
	}

	@Override
	public boolean getBooleanData(Player player, String key) {
		return getPlayerData(player).getBoolean(key, false);
	}

	@Override
	public boolean isCompleted(Quest quest, Player player) {
		return getCompletedParts(quest, player) >= quest.getQuestParts();
	}

	@Override
	public int completePart(Quest quest, Player player) {
		final YamlConfiguration data = getPlayerData(player);
		final int completedParts = data.getInt("completed-quests." + quest.getName());
		data.set("completed-quests." + quest.getName(), completedParts + 1);
		updatePlayerDataFile(player, data);
		return completedParts + 1;

	}

	@Override
	public int completePage(Player player, int page) {
		final YamlConfiguration data = getPlayerData(player);
		data.set("current-page", page);
		updatePlayerDataFile(player, data);
		return page;
	}
}
