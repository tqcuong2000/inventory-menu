package infinityi.inventorymenu.menulayout.layout;

import infinityi.inventorymenu.menulayout.MenuLayout;
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
