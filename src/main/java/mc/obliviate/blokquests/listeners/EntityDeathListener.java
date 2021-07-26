package mc.obliviate.blokquests.listeners;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.requirements.mobkillrequirement.MobKillRequirement;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityDeathListener implements Listener {

	private final BlokQuests plugin;
	private final List<EntityType> entities = new ArrayList<>();

	public EntityDeathListener(BlokQuests plugin) {
		this.plugin = plugin;
	}

	public List<EntityType> getEntities() {
		return entities;
	}

	@EventHandler
	public void onKillEntity(EntityDeathEvent e) {

		final Player player = e.getEntity().getKiller();
		if (player != null && getEntities().contains(e.getEntityType())) {

			MobKillRequirement.addKillMobs(player, e.getEntityType(), 1);

		}
	}

}
