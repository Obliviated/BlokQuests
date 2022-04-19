package mc.obliviate.blokquests.handlers;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.quest.CompleteState;
import mc.obliviate.blokquests.quest.Quest;
import mc.obliviate.blokquests.quest.QuestPage;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import mc.obliviate.blokquests.requirements.bentoboxrequirement.BentoSkyLevelRequirement;
import mc.obliviate.blokquests.requirements.economyrequirement.EconomyRequirement;
import mc.obliviate.blokquests.requirements.itemrequirement.ItemRequirement;
import mc.obliviate.blokquests.requirements.mobkillrequirement.MobKillRequirement;
import mc.obliviate.blokquests.requirements.permissionrequirement.PermissionRequirement;
import mc.obliviate.blokquests.requirements.superiorrequirement.SupSkyLevRequirement;
import mc.obliviate.blokquests.requirements.xprequirement.ExperienceRequirement;
import mc.obliviate.blokquests.rewards.QuestReward;
import mc.obliviate.blokquests.rewards.economyrewards.EconomyReward;
import mc.obliviate.blokquests.rewards.itemrewards.ItemReward;
import mc.obliviate.blokquests.utils.ConfigItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlConfigurationSerializer {

    private final BlokQuests plugin;

    public YamlConfigurationSerializer(BlokQuests plugin) {
        this.plugin = plugin;
    }

    public QuestPage getQuestPageFromFile(int page) {

        final String path = plugin.getDataFolder() + File.separator + "quest-pages" + File.separator + "page-" + page + ".yml";
        final File file = new File(path);

        if (!file.exists()) {
            plugin.getLogger().severe("File could not found: " + file);
            return null;
        }
        final YamlConfiguration qConfig = YamlConfiguration.loadConfiguration(file);

        final List<Quest> quests = new ArrayList<>();

        int lastIndex = 0;
        while (qConfig.getString("quest-" + ++lastIndex) != null) {
            quests.add(getQuest(page, lastIndex, qConfig.getConfigurationSection("quest-" + lastIndex)));
        }

        lastIndex = 0;
        final List<QuestRequirement> requirements = new ArrayList<>();
        while (qConfig.getString("page-complete.requirement-" + ++lastIndex) != null) {
            requirements.add(getQuestRequirement(qConfig.getConfigurationSection("page-complete.requirement-" + lastIndex)));
        }

        final Map<CompleteState, ConfigItem> icons = new HashMap<>();
        for (CompleteState state : CompleteState.values()) {
            final ConfigurationSection iconSection = qConfig.getConfigurationSection("page-complete.icon-states." + state.name());
            if (iconSection == null) {
                Bukkit.getLogger().severe("Unknown item configuration section found: " + qConfig.getCurrentPath() + "page-complete.icon-states." + state.name());
                continue;
            }
            icons.put(state, ConfigItem.deserializeConfigurableItem(iconSection));
        }


        return new QuestPage(icons, page, quests, requirements);

    }

    private Quest getQuest(int page, int index, ConfigurationSection section) {

        final List<QuestRequirement> requirements = new ArrayList<>();

        int lastIndex = 0;
        while (section.getString("requirement-" + ++lastIndex) != null) {
            requirements.add(getQuestRequirement(section.getConfigurationSection("requirement-" + lastIndex)));
        }

        final List<QuestReward> rewards = new ArrayList<>();

        lastIndex = 0;
        while (section.getString("reward-" + ++lastIndex) != null) {
            rewards.add(getQuestReward(section.getConfigurationSection("reward-" + lastIndex)));
        }

        final String name = section.getString("name", "unknown");
        final Map<CompleteState, ConfigItem> icons = new HashMap<>();
        final int questPart = Math.max(section.getInt("quest-parts", 1), 1);


        for (CompleteState state : CompleteState.values()) {
            final ConfigurationSection iconSection = section.getConfigurationSection("icon-states." + state.name());
            if (iconSection == null) {
                Bukkit.getLogger().severe("Unknown item configuration section found: " + section.getCurrentPath() + ".icon-states." + state.name());
                continue;
            }
            icons.put(state, ConfigItem.deserializeConfigurableItem(iconSection));
        }

        return new Quest(questPart, page, index, name, icons, requirements, rewards);
    }


    private QuestRequirement getQuestRequirement(ConfigurationSection section) {
        final String requirementType = section.getString("requirement-type");

        if (requirementType.equalsIgnoreCase("item")) {
            return ItemRequirement.deserialize(section);
        }
        if (requirementType.equalsIgnoreCase("superiorskyblock")) {
            return SupSkyLevRequirement.deserialize(section);
        }
        if (requirementType.equalsIgnoreCase("mob-kills")) {
            final MobKillRequirement requirement = MobKillRequirement.deserialize(section);
            for (EntityType entityType : requirement.getEntityTypes()) {
                if (!plugin.getEntityDeathListener().getEntities().contains(entityType)) {
                    plugin.getEntityDeathListener().getEntities().add(entityType);
                    Bukkit.getLogger().info("Entity registered to listener: " + entityType);
                }
            }
            return requirement;
        }
        if (requirementType.equalsIgnoreCase("experience")) {
            return ExperienceRequirement.deserialize(section);
        }
        if (requirementType.equalsIgnoreCase("permission")) {
            return PermissionRequirement.deserialize(section);
        }
        if (requirementType.equalsIgnoreCase("economy")) {
            return EconomyRequirement.deserialize(section);
        }
        if (requirementType.equalsIgnoreCase("bentobox")) {
            return BentoSkyLevelRequirement.deserialize(section);
        }

        throw new IllegalArgumentException("Unknown requirement type: " + requirementType);
    }

    private QuestReward getQuestReward(ConfigurationSection section) {
        final String requirementType = section.getString("reward-type");

        if (requirementType.equalsIgnoreCase("item")) {
            return ItemReward.deserialize(section);
        }
        if (requirementType.equalsIgnoreCase("economy")) {
            return EconomyReward.deserialize(section);
        }

        throw new IllegalArgumentException("Unknown reward type: " + requirementType);

    }

}
