package infinityi.inventorymenu.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UseItemEvent extends EventManager{

    public UseItemEvent(MinecraftServer server) {
        super(server);
    }

    private InteractionResult interact(Player playerEntity, Level world, InteractionHand hand) {
        if (world.isClientSide()) return InteractionResult.PASS;
        ItemStack itemStack = playerEntity.getItemInHand(hand);
        var customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.copyTag().contains("menu")) return InteractionResult.PASS;
        Identifier menuId = Identifier.parse(customData.copyTag().getStringOr("menu", ""));
        ServerPlayer player = getServerPlayer(playerEntity);
        if (player == null) {
            return InteractionResult.PASS;
        }
        return openMenu(menuId, player);
    }

    public static void register(MinecraftServer server){
        UseItemEvent useItemEvent = new UseItemEvent(server);
        UseItemCallback.EVENT.register(useItemEvent::interact);
    }
}
