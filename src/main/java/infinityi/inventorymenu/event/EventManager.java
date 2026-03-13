package infinityi.inventorymenu.event;

import infinityi.inventorymenu.InventoryMenu;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public abstract class EventManager {
    public final MinecraftServer server;

    public EventManager(MinecraftServer server) {
        this.server = server;
    }

    protected ServerPlayer getServerPlayer(@NotNull Player player) {
        return server.getPlayerList().getPlayer(player.getUUID());
    }

    protected InteractionResult openMenu(Identifier menuId, ServerPlayer player){
        var loadedMenus = InventoryMenu.dataManager.menus();
        if (!loadedMenus.hasMenu(menuId)) return InteractionResult.PASS;
        loadedMenus.getMenu(menuId).ifPresent(menuLayout -> menuLayout.open(player));
        return InteractionResult.FAIL;
    }

    public static void register(){
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            UseItemEvent.register(server);
            InteractEntityEvent.register(server);
        });

    }
}
