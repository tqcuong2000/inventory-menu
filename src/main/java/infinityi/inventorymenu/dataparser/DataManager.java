package infinityi.inventorymenu.dataparser;

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
}