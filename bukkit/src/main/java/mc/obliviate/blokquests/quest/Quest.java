package mc.obliviate.blokquests.quest;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import mc.obliviate.blokquests.rewards.QuestReward;
import mc.obliviate.blokquests.utils.BlokUtils;
import mc.obliviate.blokquests.utils.ConfigItem;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class Quest {

    private final int questPart;
    private final Map<CompleteState, ConfigItem> displayIcons;
    private final int page;
    private final int index;
    private final String name;
    private final List<QuestRequirement> requirements;
    private final List<QuestReward> rewards;

    public Quest(int questPart, int page, int index, String name, Map<CompleteState, ConfigItem> displayIcons, List<QuestRequirement> requirements, List<QuestReward> rewards) {
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
        final String name = getDisplayIcon(CompleteState.COMPLETABLE).getName();
        final TextComponent button = new TextComponent(name);


        final StringBuilder text = new StringBuilder();
        int lineNo = 3;
        int max = getDisplayIcon(CompleteState.COMPLETABLE).getLore().size();
        for (String loreLine : getDisplayIcon(CompleteState.COMPLETABLE).getLore()) {
            if (++lineNo == max) break;
            text.append(loreLine).append("\n");
        }
        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text.toString()).create()));

        final TextComponent result = new TextComponent(BlokUtils.parseColor("&6" + player.getName() + "&7 adlı oyuncu &e"));
        result.addExtra(button);
        result.addExtra(new TextComponent(BlokUtils.parseColor("&7 adlı görevi tamamladı!")));
        if (questPart != 1) {
            result.addExtra(new TextComponent(BlokUtils.parseColor(" &f(" + completedParts + "/" + questPart + ")")));
        }


        BlokUtils.log(player.getName() + " completed " + name + " (" + completedParts + "/" + questPart + ")");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(result);
        }
    }

    public CompleteState getCompleteState(Player player) {
        if (BlokQuests.getaDatabase().isCompleted(this, player)) return CompleteState.COMPLETED;
        if (canComplete(player)) {
            return CompleteState.COMPLETABLE;
        } else {
            return CompleteState.UNAFFORDABLE;
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

    public ConfigItem getDisplayIcon(CompleteState state) {
        return displayIcons.get(state);
    }

    public List<QuestRequirement> getRequirements() {
        return requirements;
    }

    public List<QuestReward> getRewards() {
        return rewards;
    }
}
