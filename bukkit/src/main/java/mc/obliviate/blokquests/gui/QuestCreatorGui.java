package mc.obliviate.blokquests.gui;

import mc.obliviate.inventory.GUI;
import org.bukkit.entity.Player;

public class QuestCreatorGui extends GUI {

    public QuestCreatorGui(Player player) {
        super(player, "quest-creator-gui", "Görev Oluştur", 5);
    }
}
