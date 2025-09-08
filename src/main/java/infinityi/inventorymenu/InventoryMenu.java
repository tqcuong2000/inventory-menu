package infinityi.inventorymenu;

import infinityi.inventorymenu.commands.MenuCommand;
import infinityi.inventorymenu.commands.TpaCommands;
import infinityi.inventorymenu.dataparser.ConfigManager;
import infinityi.inventorymenu.dataparser.DataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryMenu implements ModInitializer {

    public static final String MOD_ID = "inventory-menu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static DataManager dataManager;

    public static DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public void onInitialize() {
        ConfigManager.loadConfig();
        MenuCommand.register();
        if (ConfigManager.getConfig().enable_tpa_commands){
            TpaCommands.register();
        }
        dataManager = new DataManager();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(dataManager.getMenuDataManager());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(dataManager.items());
    }
}
