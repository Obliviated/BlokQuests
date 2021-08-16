package mc.obliviate.blokquests.quest;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import mc.obliviate.blokquests.utils.ConfigItem;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class QuestPage {

	private final Map<CompleteState, ConfigItem> pageCompleteIcon;
	private final List<Quest> quests;
	private final List<QuestRequirement> requirements;
	private final int pageNumber;

	public QuestPage(Map<CompleteState, ConfigItem> pageCompleteIcon, final int pageNumber, final List<Quest> quests, List<QuestRequirement> requirements) {
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

	public CompleteState getCompleteState(Player player) {
		if (BlokQuests.getaDatabase().isPageCompleted(this, player)) return CompleteState.COMPLETED;
		if (canCompletePage(player)) return CompleteState.COMPLETABLE;
		return CompleteState.UNAFFORDABLE;
	}

	public ConfigItem getPageCompleteIcon(CompleteState state) {
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
