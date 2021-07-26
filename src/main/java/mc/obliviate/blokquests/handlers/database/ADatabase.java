package mc.obliviate.blokquests.handlers.database;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.quest.Quest;
import mc.obliviate.blokquests.quest.QuestPage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class ADatabase {

	private final BlokQuests plugin;

	public ADatabase(BlokQuests plugin) {
		this.plugin = plugin;
	}

	public BlokQuests getPlugin() {
		return plugin;
	}

	public abstract boolean isCompleted(Quest quest, Player player);
	public abstract int completePart(Quest quest, Player player);
	public abstract int completePage(Player player, int page);
	public abstract int getCompletedParts(Quest quest, Player player);
	public abstract int getCompletedPages(Player player);
	public abstract boolean isPageCompleted(QuestPage page, Player player);
	public abstract List<Quest> getCompletedQuests(Player player);
	public abstract void addData(Player player, String key, Object value);
	public abstract int getIntegerData(Player player, String key);
	public abstract boolean getBooleanData(Player player, String key);

}
