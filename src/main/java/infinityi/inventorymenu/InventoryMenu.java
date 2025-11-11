package infinityi.inventorymenu;

import infinityi.inventorymenu.command.MenuCommand;
import infinityi.inventorymenu.dataparser.ConfigManager;
import infinityi.inventorymenu.dataparser.DataManager;
import infinityi.inventorymenu.event.EventManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryMenu implements ModInitializer {

    public static final String MOD_ID = "inventory-menu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static DataManager dataManager;

    @Override
    public void onInitialize() {
        ConfigManager.loadConfig();
        MenuCommand.register();
        EventManager.register();
        dataManager = new DataManager();
        dataManager.register();
        ConfigManager.saveConfig();
    }
}
