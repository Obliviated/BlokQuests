package mc.obliviate.blokquests.gui;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.quest.Quest;
import mc.obliviate.blokquests.quest.QuestCompleteState;
import mc.obliviate.blokquests.quest.QuestPage;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import mc.obliviate.blokquests.requirements.itemrequirement.ItemRequirement;
import mc.obliviate.blokquests.requirements.mobkillrequirement.MobKillRequirement;
import mc.obliviate.blokquests.utils.BlokUtils;
import mc.obliviate.blokquests.utils.ConfigItem;
import mc.obliviate.blokquests.utils.internalplaceholder.PlaceholderUtil;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import xyz.efekurbann.inventory.GUI;
import xyz.efekurbann.inventory.Hytem;

import java.util.ArrayList;
import java.util.List;

public class QuestPageGui extends GUI {

	private final QuestPage questPage;

	public QuestPageGui(QuestPage questPage) {
		super("quest-page-gui", "Görev Sayfası " + questPage.getPageNumber(), 5);
		this.questPage = questPage;
	}

	private static void showRequirementItems(GUI gui, List<QuestRequirement> questRequirements) {
		resetRequirementItems(gui);

		final List<ItemStack> displayItems = new ArrayList<>();
		for (QuestRequirement requirement : questRequirements) {
			if (requirement instanceof ItemRequirement) {
				for (ConfigItem configItem : ((ItemRequirement) requirement).getItemStackList()) {
					displayItems.add(configItem.toItemStack());
				}
			}
		}
		for (int i = 0; i < displayItems.size(); i++) {

			gui.addItem(i + 29, new Hytem(displayItems.get(i)));
		}

	}

	private static void resetRequirementItems(GUI gui) {
		for (int i = 29; i < 34; i++) {
			gui.addItem(i, new Hytem(Material.STAINED_GLASS_PANE).setDamage(0));
		}
	}

	private static PlaceholderUtil getPlaceholders(Quest quest, Player player) {

		return new PlaceholderUtil()
				.add("{quest_name}", quest.getName())
				.add("{quest_completed_parts}", BlokQuests.getaDatabase().getCompletedParts(quest, player) + "")
				.add("{quest_parts}", quest.getQuestParts() + "")
				.add("{player_name}", player.getName() + "")
				.add("{zombie_kills}", MobKillRequirement.getMobKills(player,EntityType.ZOMBIE) + "")

				;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		final Player player = (Player) event.getPlayer();
		int completedPages = BlokQuests.getaDatabase().getCompletedPages(player);
		if (completedPages + 1 < questPage.getPageNumber()) {
			Bukkit.getScheduler().runTaskLater(getPlugin(), player::closeInventory, 1);
			player.sendMessage("bu sayfayı henüz açmadınız: " + completedPages);
			return;
		}

		for (int i = 0; i < 5; i++) {
			addItem(i * 9, new Hytem(new ItemStack(Material.STAINED_GLASS_PANE)).setDamage(7));
			addItem(i * 9 + 8, new Hytem(new ItemStack(Material.STAINED_GLASS_PANE)).setDamage(7));
		}

		if (completedPages >= questPage.getPageNumber()) {

			addItem(8, new Hytem(new ItemStack(Material.ARROW), e -> {
				final QuestPage page = ((BlokQuests) getPlugin()).getDataHandler().getQuestPage(questPage.getPageNumber() + 1);
				if (page == null) return;
				new QuestPageGui(page).open(player);
			}).setName(BlokUtils.parseColor("&eSıradaki sayfa")));
		}
		if (questPage.getPageNumber() != 1) {
			addItem(0, new Hytem(new ItemStack(Material.ARROW), e -> {
				final QuestPage page = ((BlokQuests) getPlugin()).getDataHandler().getQuestPage(questPage.getPageNumber() - 1);
				if (page == null) return;
				new QuestPageGui(page).open(player);
			}).setName(BlokUtils.parseColor("&eÖnceki sayfa")));
		}


		QuestCompleteState pageCompleteState = questPage.getCompleteState(player);
		switch (pageCompleteState) {
			case COMPLETED:
				addItem(26, new Hytem(questPage.getPageCompleteIcon(pageCompleteState).toItemStack()));
				break;
			case COMPLETABLE:
				addItem(26, new Hytem(questPage.getPageCompleteIcon(pageCompleteState).toItemStack(), e -> {
					questPage.completePage(player);
					new QuestPageGui(questPage).open(player);
				}));
				break;
			case UNAFFORDABLE:
				addItem(26, questPage.getPageCompleteIcon(pageCompleteState).toItemStack());
				break;
		}

		resetRequirementItems(this);

		int i = 11;
		for (final Quest quest : questPage.getQuests()) {
			addItem(i++, getHytem(quest, player));
		}
	}

	private Hytem getCompletableStateHytem(Quest quest, PlaceholderUtil placeholders) {
		return new Hytem(quest.getDisplayIcon(QuestCompleteState.COMPLETABLE).toItemStack(placeholders), e -> {
			if (e.isLeftClick()) {
				final Player clicker = (Player) e.getWhoClicked();
				if (quest.canComplete(clicker)) {
					quest.complete(clicker);
					new QuestPageGui(questPage).open(clicker);
				} else {
					clicker.sendMessage("Karşılamıyorsunuz.");
				}
			} else if (e.isRightClick()) {
				showRequirementItems(this, quest.getRequirements());
			}
		});
	}

	private Hytem getCompletedStateHytem(Quest quest, PlaceholderUtil placeholders) {
		return new Hytem(quest.getDisplayIcon(QuestCompleteState.COMPLETED).toItemStack(placeholders), e -> {
			showRequirementItems(this, quest.getRequirements());
		});
	}

	private Hytem getUnaffordableStateHytem(Quest quest, PlaceholderUtil placeholders) {
		return new Hytem(quest.getDisplayIcon(QuestCompleteState.UNAFFORDABLE).toItemStack(placeholders), e -> {
			if (e.isLeftClick()) {
				e.getWhoClicked().sendMessage("Karşılamıyorsunuz.");
			} else if (e.isRightClick()) {
				showRequirementItems(this, quest.getRequirements());
			}
		});
	}

	public Hytem getHytem(Quest quest, Player player) {
		final PlaceholderUtil placeholders = getPlaceholders(quest, player);
		final QuestCompleteState state = quest.getCompleteState(player);
		switch (state) {
			case UNAFFORDABLE:
				return getUnaffordableStateHytem(quest, placeholders);
			case COMPLETABLE:
				return getCompletableStateHytem(quest, placeholders);
			case COMPLETED:
				return getCompletedStateHytem(quest, placeholders);
		}
		return null;
	}

}
