package mc.obliviate.blokquests.commands;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.gui.QuestPageGui;
import mc.obliviate.blokquests.quest.QuestPage;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class QuestCmd implements CommandExecutor {

	private final BlokQuests plugin;

	public QuestCmd(BlokQuests plugin) {
		this.plugin = plugin;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("Bu komut konsol tarafından kullanılamaz.");
			return false;
		}
		if (args.length == 0) {
			sender.sendMessage("sayfa sayısı girmelisiniz.");
			return false;
		}

		final Player player = (Player) sender;

		int page = Integer.parseInt(args[0]);

		final QuestPage questPage = plugin.getYamlDatabaseHandler().getQuestPageFromFile(page);
		new QuestPageGui(questPage).open(player);



		return true;
	}
}
