package infinityi.inventorymenu.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UseItemEvent extends EventManager {

    public static void register() {
        UseItemCallback.EVENT.register(UseItemEvent::interact);
    }

    private static InteractionResult interact(Player playerEntity, Level world, InteractionHand hand) {
        if (world.isClientSide() || !(playerEntity instanceof ServerPlayer player)) return InteractionResult.PASS;
        ItemStack itemStack = player.getItemInHand(hand);
        var customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.copyTag().contains("menu")) return InteractionResult.PASS;
        String menuTag = customData.copyTag().getStringOr("menu", "");
        Identifier menuId = Identifier.tryParse(menuTag);
        if (menuId == null) return InteractionResult.PASS;
        return openMenu(menuId, player);
    }
}
