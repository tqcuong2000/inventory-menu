package infinityi.inventorymenu;

import infinityi.inventorymenu.command.MenuCommand;
import infinityi.inventorymenu.dataparser.ConfigManager;
import infinityi.inventorymenu.dataparser.DataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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
        dataManager = new DataManager();

        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(Identifier.of(InventoryMenu.MOD_ID, "menu_data_manager"), dataManager.menus());
        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(Identifier.of(InventoryMenu.MOD_ID, "item_menu_data_manager"), dataManager.items());
    }
}
