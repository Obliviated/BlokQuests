package mc.obliviate.blokquests.handlers;

import mc.obliviate.blokquests.BlokQuests;
import mc.obliviate.blokquests.quest.QuestPage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigHandler {

	private final BlokQuests plugin;
	private YamlConfiguration config;
	private static File logFile;

	public ConfigHandler(BlokQuests plugin) {
		this.plugin = plugin;
		logFile = new File(plugin.getDataFolder() + File.separator + "quest-complete-logs.log");
	}

	public void load() {
		loadConfig();
		loadQuestPages();
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	private void loadConfig() {
		final String path = "config.yml";
		final File configFile = new File(plugin.getDataFolder() + File.separator + path);
		config = YamlConfiguration.loadConfiguration(configFile);
		if (!configFile.exists()) {
			plugin.saveResource(path, false);
		}
	}

	private void loadQuestPages() {

		final String path = "quest-pages" + File.separator + "page-1.yml";
		File exampleConfigFile = new File(plugin.getDataFolder() + File.separator + path);
		if (!exampleConfigFile.exists()) {
			plugin.saveResource(path, false);
		}

		int i = 0;
		while (true) {
			File file = new File(plugin.getDataFolder() + File.separator + "quest-pages" + File.separator + "page-" + ++i + ".yml");
			if (!file.exists()) {
				break;
			}

			QuestPage questPage = plugin.getYamlDatabaseHandler().getQuestPageFromFile(i);

			if (questPage == null) {
				plugin.getLogger().info("quest page is null");
				continue;
			}

			plugin.getDataHandler().registerQuestPage(i, questPage);
		}
	}

	public static File getLogFile() {
		return logFile;
	}
}
