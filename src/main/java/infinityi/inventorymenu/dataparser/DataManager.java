package infinityi.inventorymenu.dataparser;

import infinityi.inventorymenu.InventoryMenu;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class DataManager {
    private final MenuDataManager menuDataManager;
    private final ItemDataManager itemDataManager;
    private final PlayerData playerData;

    public DataManager() {
        this.itemDataManager = new ItemDataManager();
        this.menuDataManager = new MenuDataManager();
        this.playerData = new PlayerData();
    }

    public MenuDataManager menus() {return menuDataManager;}

    public ItemDataManager items() {return itemDataManager;}

    public PlayerData playerData() {return playerData;}

    public void register(){
        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(Identifier.of(InventoryMenu.MOD_ID, "menu_data_manager"), this.menus());
        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(Identifier.of(InventoryMenu.MOD_ID, "item_menu_data_manager"), this.items());
    }
}