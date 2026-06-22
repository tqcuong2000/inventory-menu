package infinityi.inventorymenu.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class UseItemEvent extends EventManager {
    private UseItemEvent() {
    }

    public static InteractionResult interact(ServerPlayer player, Level world, InteractionHand hand) {
        if (world.isClientSide()) return InteractionResult.PASS;
        ItemStack itemStack = player.getItemInHand(hand);
        var customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.copyTag().contains("menu")) return InteractionResult.PASS;
        Identifier menuId = Identifier.parse(customData.copyTag().getStringOr("menu", ""));
        return openMenu(menuId, player);
    }

    public static void register() {
    }
}
