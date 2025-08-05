package infinityi.inventoryMenu.DataParser;

public class DataManager {
    private final MenuDataManager menuDataManager;

    public DataManager() {
        this.menuDataManager = new MenuDataManager();
    }

    public MenuDataManager menus() {
        return menuDataManager;
    }

    public MenuDataManager getMenuDataManager() {
        return menuDataManager;
    }
}