package mc.obliviate.blokquests.handlers;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.quest.Quest;
import mc.obliviate.blokquests.quest.QuestPage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataHandler {

	private final BlokQuests plugin;

	private final Map<Integer, QuestPage> questPages = new HashMap<>();
	//todo
	//                         actually yaml configuration is not best way
	//                    because the plugin will be hard coded to based yaml database.
	//                      if you're developer that wants another database, create a
	//                       user object to use best way i do not have time for that
	//
	private final Map<UUID, YamlConfiguration> datas = new HashMap<>();

	public DataHandler(BlokQuests plugin) {
		this.plugin = plugin;
	}

	public void registerQuestPage(int id, QuestPage page) {
		questPages.put(id, page);
		plugin.getLogger().info("registering quest page: " + id + "-" + page.getQuests());
	}

	public Quest findQuest(String questName) {
		for (QuestPage questPage : questPages.values()) {
			for (Quest quest : questPage.getQuests()) {
				if (quest.getName().equalsIgnoreCase(questName)) {
					return quest;
				}
			}
		}
		return null;
	}

	public Map<Integer, QuestPage> getQuestPages() {
		return questPages;
	}

	public QuestPage getQuestPage(int pageNumber) {
		return questPages.get(pageNumber);
	}

	public Map<UUID, YamlConfiguration> getDatas() {
		return datas;
	}
}
