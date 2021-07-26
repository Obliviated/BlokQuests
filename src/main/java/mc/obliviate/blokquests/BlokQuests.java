package mc.obliviate.blokquests;

import mc.obliviate.blokquests.commands.QuestCmd;
import mc.obliviate.blokquests.handlers.ConfigHandler;
import mc.obliviate.blokquests.handlers.DataHandler;
import mc.obliviate.blokquests.handlers.YamlConfigurationSerializer;
import mc.obliviate.blokquests.handlers.database.ADatabase;
import mc.obliviate.blokquests.handlers.database.YamlDatabase;
import mc.obliviate.blokquests.listeners.EntityDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.efekurbann.inventory.InventoryAPI;

public class BlokQuests extends JavaPlugin {

	private static ADatabase aDatabase;
	private final YamlConfigurationSerializer yamlConfigurationSerializer = new YamlConfigurationSerializer(this);
	private final DataHandler dataHandler = new DataHandler(this);
	private final ConfigHandler configHandler = new ConfigHandler(this);
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final EntityDeathListener entityDeathListener = new EntityDeathListener(this);

	public static ADatabase getaDatabase() {
		return aDatabase;
	}

	@Override
	public void onEnable() {
		setupHandlers();
		registerCommands();
		registerListeners();
	}

	private void registerCommands() {
		getCommand("quest").setExecutor(new QuestCmd(this));
	}

	private void setupHandlers() {
		configHandler.load();
		inventoryAPI.init();

		aDatabase = connectDatabase();
	}

	public void registerListeners() {
		Bukkit.getPluginManager().registerEvents(entityDeathListener, this);
	}

	private ADatabase connectDatabase() {
		String databaseType = getConfigHandler().getConfig().getString("database-type");
		if (databaseType.equalsIgnoreCase("yaml")) {
			return new YamlDatabase(this);
		}
		throw new IllegalStateException("Database Type could not deserialized.");
	}

	public YamlConfigurationSerializer getYamlDatabaseHandler() {
		return yamlConfigurationSerializer;
	}

	public DataHandler getDataHandler() {
		return dataHandler;
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public EntityDeathListener getEntityDeathListener() {
		return entityDeathListener;
	}
}