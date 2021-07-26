package mc.obliviate.blokquests.quest;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import mc.obliviate.blokquests.utils.BlokUtils;
import mc.obliviate.blokquests.utils.ConfigItem;
import mc.obliviate.blokquests.rewards.QuestReward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class Quest {

	private final int questPart;
	private final Map<QuestCompleteState, ConfigItem> displayIcons;
	private final int page;
	private final int index;
	private final String name;
	private final List<QuestRequirement> requirements;
	private final List<QuestReward> rewards;

	public Quest(int questPart, int page, int index, String name, Map<QuestCompleteState, ConfigItem> displayIcons, List<QuestRequirement> requirements, List<QuestReward> rewards) {
		this.questPart = questPart;
		this.page = page;
		this.index = index;
		this.name = name;
		this.requirements = requirements;
		this.rewards = rewards;
		this.displayIcons = displayIcons;
	}

	public boolean canComplete(final Player player) {
		for (QuestRequirement qReq : requirements) {
			if (!qReq.canMeet(player)) {
				return false;
			}
		}
		return true;
	}

	public void complete(final Player player) {
		int completedParts = BlokQuests.getaDatabase().completePart(this, player);
		for (QuestRequirement qReq : requirements) {
			qReq.removeRequirements(player);
		}
		for (QuestReward qRew : rewards) {
			qRew.deliverRewards(player);
		}
		BlokUtils.log(player.getName() + " completed " + name + " (" + completedParts + "/" + questPart + ")");
		if (questPart == 1) {
			Bukkit.broadcastMessage(BlokUtils.parseColor("&6" + player.getName() + "&7 adlı oyuncu &e" + name + "&7 adlı görevi tamamladı."));
		} else {
			Bukkit.broadcastMessage(BlokUtils.parseColor("&6" + player.getName() + "&7 adlı oyuncu &e" + name + "&7 adlı görevi tamamladı &f(" + completedParts + "/" + questPart + ")"));
		}
	}

	public QuestCompleteState getCompleteState(Player player) {
		if (BlokQuests.getaDatabase().isCompleted(this, player)) return QuestCompleteState.COMPLETED;
		if (canComplete(player)) {
			return QuestCompleteState.COMPLETABLE;
		} else {
			return QuestCompleteState.UNAFFORDABLE;
		}
	}

	public int getPage() {
		return page;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getQuestParts() {
		return questPart;
	}

	public ConfigItem getDisplayIcon(QuestCompleteState state) {
		return displayIcons.get(state);
	}

	public List<QuestRequirement> getRequirements() {
		return requirements;
	}

	public List<QuestReward> getRewards() {
		return rewards;
	}
}
