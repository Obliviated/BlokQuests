package mc.obliviate.blokquests.quest;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import mc.obliviate.blokquests.utils.ConfigItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class QuestPage {

	private final Map<QuestCompleteState, ConfigItem> pageCompleteIcon;
	private final List<Quest> quests;
	private final List<QuestRequirement> requirements;
	private final int pageNumber;

	public QuestPage(Map<QuestCompleteState, ConfigItem> pageCompleteIcon, final int pageNumber, final List<Quest> quests, List<QuestRequirement> requirements) {
		this.pageCompleteIcon = pageCompleteIcon;
		this.pageNumber = pageNumber;
		this.quests = quests;
		this.requirements = requirements;
	}

	public boolean canCompletePage(final Player player) {
		if (BlokQuests.getaDatabase().isPageCompleted(this, player)) return false;
		for (Quest quest : quests) {
			if (!BlokQuests.getaDatabase().isCompleted(quest, player)) {
				return false;
			}
		}
		for (QuestRequirement requirements : requirements) {
			if (!requirements.canMeet(player)) {
				return false;
			}
		}

		return true;
	}

	public QuestCompleteState getCompleteState(Player player) {
		if (BlokQuests.getaDatabase().isPageCompleted(this, player)) return QuestCompleteState.COMPLETED;
		if (canCompletePage(player)) return QuestCompleteState.COMPLETABLE;
		return QuestCompleteState.UNAFFORDABLE;
	}

	public ConfigItem getPageCompleteIcon(QuestCompleteState state) {
		return pageCompleteIcon.get(state);
	}

	public void completePage(final Player player) {
		BlokQuests.getaDatabase().completePage(player, pageNumber);
	}

	public List<Quest> getQuests() {
		return quests;
	}

	public int getPageNumber() {
		return pageNumber;
	}
}
