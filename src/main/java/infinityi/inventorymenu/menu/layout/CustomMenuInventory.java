package infinityi.inventorymenu.menu.layout;

import infinityi.inventorymenu.menu.MenuLayout;
import net.minecraft.inventory.SimpleInventory;

public class CustomMenuInventory extends SimpleInventory {
    private final MenuLayout layout;

    public CustomMenuInventory(MenuLayout layout, int size) {
        super(size);
        this.layout = layout;
    }

    public MenuLayout getLayout() {
        return this.layout;
    }
}
