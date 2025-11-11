package infinityi.inventorymenu.event;

import infinityi.inventorymenu.InventoryMenu;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class UseItemEvent{

    public final MinecraftServer server;

    public UseItemEvent(MinecraftServer server) {
        this.server = server;
    }

    private ActionResult interact(PlayerEntity playerEntity, World world, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        var customData = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null || !customData.copyNbt().contains("menu")) return ActionResult.PASS;
        Identifier menuId = Identifier.of(customData.copyNbt().getString("menu", ""));
        var loadedMenus = InventoryMenu.dataManager.menus();
        if (!loadedMenus.hasMenu(menuId)) return ActionResult.PASS;
        loadedMenus.getMenu(menuId).ifPresent(menuLayout -> {
            menuLayout.open(server.getPlayerManager().getPlayer(playerEntity.getUuid()));
        });
        return ActionResult.CONSUME;
    }

    public static void register(){
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            UseItemEvent useItemEvent = new UseItemEvent(server);
            UseItemCallback.EVENT.register(useItemEvent::interact);
        });

    }
}
