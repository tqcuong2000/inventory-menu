package infinityi.inventorymenu;

import infinityi.inventorymenu.commands.MenuCommand;
import infinityi.inventorymenu.commands.TpaCommands;
import infinityi.inventorymenu.dataparser.DataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryMenu implements ModInitializer {

    public static final String MOD_ID = "inventory-menu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Text PREFIX = Text.literal("[InventoryMenu] ").formatted(Formatting.LIGHT_PURPLE);

    private static DataManager dataManager;

    @Override
    public void onInitialize() {
        TpaCommands.register();
        MenuCommand.register();
        dataManager = new DataManager();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(dataManager.getMenuDataManager());
    }

    public static DataManager getDataManager() {
        return dataManager;
    }
}
