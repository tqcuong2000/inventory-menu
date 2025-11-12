package infinityi.inventorymenu.event;

import infinityi.inventorymenu.InventoryMenu;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public abstract class EventManager {
    public final MinecraftServer server;

    public EventManager(MinecraftServer server) {
        this.server = server;
    }

    protected ServerPlayerEntity getServerPlayer(@NotNull PlayerEntity player) {
        return server.getPlayerManager().getPlayer(player.getUuid());
    }

    protected ActionResult openMenu(Identifier menuId, ServerPlayerEntity player){
        var loadedMenus = InventoryMenu.dataManager.menus();
        if (!loadedMenus.hasMenu(menuId)) return ActionResult.PASS;
        loadedMenus.getMenu(menuId).ifPresent(menuLayout -> menuLayout.open(player));
        return ActionResult.FAIL;
    }

    public static void register(){
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            UseItemEvent.register(server);
            InteractEntityEvent.register(server);
        });

    }
}
