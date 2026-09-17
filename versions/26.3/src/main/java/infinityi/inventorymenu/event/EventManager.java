package infinityi.inventorymenu.event;

import infinityi.inventorymenu.InventoryMenu;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public abstract class EventManager {
    protected static InteractionResult openMenu(Identifier menuId, ServerPlayer player) {
        var loadedMenus = InventoryMenu.dataManager.menus();
        if (!loadedMenus.hasMenu(menuId)) return InteractionResult.PASS;
        loadedMenus.getMenu(menuId).ifPresent(menuLayout -> menuLayout.open(player));
        return InteractionResult.FAIL;
    }

    public static void register() {
    }
}
