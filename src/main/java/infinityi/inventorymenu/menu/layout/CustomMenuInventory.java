package infinityi.inventorymenu.menu.layout;

import infinityi.inventorymenu.menu.MenuLayout;
import net.minecraft.world.SimpleContainer;

public class CustomMenuInventory extends SimpleContainer {
    private final MenuLayout layout;

    public CustomMenuInventory(MenuLayout layout, int size) {
        super(size);
        this.layout = layout;
    }

    public MenuLayout getLayout() {
        return this.layout;
    }
}
