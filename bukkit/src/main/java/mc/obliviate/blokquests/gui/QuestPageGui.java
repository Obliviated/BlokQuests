package mc.obliviate.blokquests.gui;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.quest.CompleteState;
import mc.obliviate.blokquests.quest.Quest;
import mc.obliviate.blokquests.quest.QuestPage;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import mc.obliviate.blokquests.requirements.itemrequirement.ItemRequirement;
import mc.obliviate.blokquests.requirements.mobkillrequirement.MobKillRequirement;
import mc.obliviate.blokquests.utils.BlokUtils;
import mc.obliviate.blokquests.utils.ConfigItem;
import mc.obliviate.blokquests.utils.internalplaceholder.PlaceholderUtil;
import mc.obliviate.blokquests.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QuestPageGui extends GUI {

    private final QuestPage questPage;

    public QuestPageGui(Player player, QuestPage questPage) {
        super(player, "quest-page-gui", "Görev Sayfası: " + questPage.getPageNumber(), 5);
        this.questPage = questPage;
    }

    private static void showRequirementItems(GUI gui, List<QuestRequirement> questRequirements) {
        resetRequirementItems(gui);

        final List<ItemStack> displayItems = new ArrayList<>();
        for (QuestRequirement requirement : questRequirements) {
            if (requirement instanceof ItemRequirement) {
                for (ConfigItem configItem : ((ItemRequirement) requirement).getItemStackList()) {
                    displayItems.addAll(splitItemAmount(configItem.toItemStack(), configItem.getAmount()));
                }
            }
        }

        for (int i = 0; i < Math.min(displayItems.size(), 5); i++) {
            gui.addItem(i + 29, new Icon(displayItems.get(i)));
        }


    }

    private static List<ItemStack> splitItemAmount(ItemStack item, int amount) {
        final List<ItemStack> result = new ArrayList<>();
        while (amount > 0) {
            ItemStack resultItem = item.clone();
            resultItem.setAmount(Math.min(64, amount));
            result.add(resultItem);
            amount -= 64;
        }
        return result;
    }

    private static void resetRequirementItems(GUI gui) {
        for (int i = 29; i < 34; i++) {
            gui.addItem(i, new Icon(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()));
        }
    }

    private static PlaceholderUtil getPlaceholders(Quest quest, Player player) {

        return new PlaceholderUtil()
                .add("{quest_name}", quest.getName())
                .add("{quest_completed_parts}", BlokQuests.getaDatabase().getCompletedParts(quest, player) + "")
                .add("{quest_parts}", quest.getQuestParts() + "")
                .add("{player_name}", player.getName() + "")
                .add("{skeleton_kills}", MobKillRequirement.getMobKills(player, EntityType.SKELETON) + "")
                .add("{creeper_kills}", MobKillRequirement.getMobKills(player, EntityType.CREEPER) + "")
                .add("{zombie_kills}", MobKillRequirement.getMobKills(player, EntityType.ZOMBIE) + "")
                .add("{dragon_kills}", MobKillRequirement.getMobKills(player, EntityType.ENDER_DRAGON) + "");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        final Player player = (Player) event.getPlayer();
        int completedPages = BlokQuests.getaDatabase().getCompletedPages(player);
        if (completedPages + 1 < questPage.getPageNumber() && !player.isOp()) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), player::closeInventory, 1);
            player.sendMessage(BlokUtils.parseColor("&cBu sayfayı henüz açmadınız: &7" + questPage.getPageNumber()));
            return;
        }

        for (int i = 0; i < 5; i++) {
            addItem(i * 9, new Icon(Material.BLACK_STAINED_GLASS_PANE));
            addItem(i * 9 + 8, new Icon(Material.BLACK_STAINED_GLASS_PANE));
        }

        if (completedPages >= questPage.getPageNumber()) {
            addItem(8, new Icon(XMaterial.ARROW.parseItem()).onClick(event1 -> {
                final QuestPage page = ((BlokQuests) getPlugin()).getDataHandler().getQuestPage(questPage.getPageNumber() + 1);
                if (page == null) return;
                new QuestPageGui(player, page).open();
            }).setName(BlokUtils.parseColor("&eSıradaki sayfa")));

        }
        if (questPage.getPageNumber() != 1) {
            addItem(0, new Icon(new ItemStack(Material.ARROW)).onClick(event1 -> {
                final QuestPage page = ((BlokQuests) getPlugin()).getDataHandler().getQuestPage(questPage.getPageNumber() - 1);
                if (page == null) return;
                new QuestPageGui(player, page).open();
            }).setName(BlokUtils.parseColor("&eÖnceki sayfa")));
        }


        CompleteState pageCompleteState;
        if (((BlokQuests) getPlugin()).getDataHandler().getQuestPage(questPage.getPageNumber() + 1) == null) {
            pageCompleteState = CompleteState.UNAFFORDABLE;
        } else {
            pageCompleteState = questPage.getCompleteState(player);
        }
        switch (pageCompleteState) {
            case COMPLETED:
                addItem(26, new Icon(questPage.getPageCompleteIcon(pageCompleteState).toItemStack()));
                break;
            case COMPLETABLE:
                addItem(26, new Icon(questPage.getPageCompleteIcon(pageCompleteState).toItemStack()).onClick(event1 -> {
                    if (questPage.getCompleteState(player).equals(CompleteState.COMPLETABLE)) {
                        questPage.completePage(player);
                        new QuestPageGui(player, questPage).open();
                    }
                }));
                break;
            case UNAFFORDABLE:
                addItem(26, questPage.getPageCompleteIcon(pageCompleteState).toItemStack());
                break;
        }


        resetRequirementItems(this);

        int i = 20;
        for (final Quest quest : questPage.getQuests()) {
            addItem(i++, getHytem(quest, player));
        }
    }

    private Icon getCompletableStateHytem(Quest quest, PlaceholderUtil placeholders) {
        return new Icon(quest.getDisplayIcon(CompleteState.COMPLETABLE).toItemStack(placeholders)).onClick(e -> {
            if (e.isLeftClick()) {
                final Player clicker = (Player) e.getWhoClicked();
                if (quest.canComplete(clicker)) {
                    quest.complete(clicker);
                    new QuestPageGui(clicker, questPage).open();
                } else {
                    clicker.sendMessage(BlokUtils.parseColor("&cGereksinimleri karşılayacak kadar eşyaya sahip değilsiniz."));
                }
            } else if (e.isRightClick()) {
                showRequirementItems(this, quest.getRequirements());
            }
        });
    }

    private Icon getCompletedStateHytem(Quest quest, PlaceholderUtil placeholders) {
        return new Icon(quest.getDisplayIcon(CompleteState.COMPLETED).toItemStack(placeholders)).onClick(e -> {
            showRequirementItems(this, quest.getRequirements());
        });
    }

    private Icon getUnaffordableStateHytem(Quest quest, PlaceholderUtil placeholders) {
        return new Icon(quest.getDisplayIcon(CompleteState.UNAFFORDABLE).toItemStack(placeholders)).onClick(e -> {
            if (e.isLeftClick()) {
                e.getWhoClicked().sendMessage(BlokUtils.parseColor("&cGereksinimleri karşılayacak kadar eşyaya sahip değilsiniz."));
            } else if (e.isRightClick()) {
                showRequirementItems(this, quest.getRequirements());
            }
        });
    }

    public Icon getHytem(Quest quest, Player player) {
        final PlaceholderUtil placeholders = getPlaceholders(quest, player);
        final CompleteState state = quest.getCompleteState(player);
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
