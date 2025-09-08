package infinityi.inventorymenu.dataparser;

public class DataManager {
    private final MenuDataManager menuDataManager;
    private final ItemDataManager itemDataManager;

    public DataManager() {
        this.itemDataManager = new ItemDataManager();
        this.menuDataManager = new MenuDataManager();
    }

    public MenuDataManager menus() {
        return menuDataManager;
    }

    public MenuDataManager getMenuDataManager() {
        return menuDataManager;
    }

    public ItemDataManager items() {return itemDataManager;}
}