package mc.obliviate.blokquests.commands;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.gui.QuestPageGui;
import mc.obliviate.blokquests.quest.QuestPage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
		final Player player = (Player) sender;
		int page = -1;
		if (args.length == 0) {
			page = BlokQuests.getaDatabase().getCompletedPages(player) + 1;
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (!player.isOp()) return false;
			player.sendMessage("§aYüklenio");
			plugin.getConfigHandler().load();
			player.sendMessage("§aYüklendikek");
		} else {
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				player.sendMessage("§cGeçersiz bir sayfa sayısı belirttiniz.");
				return false;
			}
		}

		page = Math.max(page, 1);
		page = Math.min(page, plugin.getDataHandler().getQuestPages().size());
		final QuestPage questPage = plugin.getYamlSerializer().getQuestPageFromFile(page);
		new QuestPageGui(questPage).open(player);


		return true;
	}
}
