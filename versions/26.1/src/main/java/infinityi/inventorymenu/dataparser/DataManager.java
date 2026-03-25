package infinityi.inventorymenu.dataparser;

import java.util.List;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class DataManager {

    private final MenuDataManager menuDataManager;
    private final ItemDataManager itemDataManager;
    private final PlayerData playerData;

    public DataManager() {
        this.itemDataManager = new ItemDataManager();
        this.menuDataManager = new MenuDataManager();
        this.playerData = new PlayerData();
    }

    public MenuDataManager menus() {
        return menuDataManager;
    }

    public ItemDataManager items() {
        return itemDataManager;
    }

    public PlayerData playerData() {
        return playerData;
    }

    public void register() {}

    public List<PreparableReloadListener> reloadListeners() {
        return List.of(this.menus(), this.items());
    }
}
