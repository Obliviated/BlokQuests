package mc.obliviate.blokquests.requirements.permissionrequirement;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.requirements.QuestRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionRequirement implements QuestRequirement {

    private final String permission;

    private PermissionRequirement(String permission) {
        this.permission = permission;
    }

    public static PermissionRequirement deserialize(ConfigurationSection section) {
        return new PermissionRequirement(section.getString("permission"));
    }

    @Override
    public boolean canMeet(Player player) {
        return BlokQuests.getPermission().has(player, permission);
    }

    @Override
    public void removeRequirements(Player player) {

    }
}
