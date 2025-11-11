package infinityi.inventorymenu.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
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
        return openMenu(menuId, getServerPlayer(playerEntity));
    }

    public static void register(MinecraftServer server){
        UseItemEvent useItemEvent = new UseItemEvent(server);
        UseItemCallback.EVENT.register(useItemEvent::interact);
    }
}
