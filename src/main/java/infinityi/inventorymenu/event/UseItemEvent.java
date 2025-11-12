package infinityi.inventorymenu.event;

import infinityi.inventorymenu.InventoryMenu;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class UseItemEvent extends EventManager{

    public UseItemEvent(MinecraftServer server) {
        super(server);
    }

    private ActionResult interact(PlayerEntity playerEntity, World world, Hand hand) {
        if (world.isClient()) return ActionResult.PASS;
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        var customData = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null || !customData.copyNbt().contains("menu")) return ActionResult.PASS;
        Identifier menuId = Identifier.of(customData.copyNbt().getString("menu", ""));
        ServerPlayerEntity player = getServerPlayer(playerEntity);
        if (player == null) {
            return ActionResult.PASS;
        }
        return openMenu(menuId, player);
    }

    public static void register(MinecraftServer server){
        UseItemEvent useItemEvent = new UseItemEvent(server);
        UseItemCallback.EVENT.register(useItemEvent::interact);
    }
}
