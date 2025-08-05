package infinityi.inventoryMenu;

import infinityi.inventoryMenu.Commands.MenuCommand;
import infinityi.inventoryMenu.Commands.TpaCommands;
import infinityi.inventoryMenu.DataParser.MenuDataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryMenu implements ModInitializer {

    public static final String MOD_ID = "inventory-menu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Text PREFIX = Text.literal("[InventoryMenu] ").setStyle(Style.EMPTY.withItalic(false)).formatted(Formatting.LIGHT_PURPLE);

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MenuDataManager());
        TpaCommands.register();
        MenuCommand.register();
    }
}
