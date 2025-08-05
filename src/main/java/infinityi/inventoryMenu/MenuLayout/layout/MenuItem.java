package infinityi.inventoryMenu.MenuLayout.layout;

import com.mojang.serialization.Codec;
import infinityi.inventoryMenu.ItemAction.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;

public interface MenuItem {
    Codec<MenuItem> CODEC = StringIdentifiable.createCodec(MenuItemType::values)
            .dispatch(MenuItem::getType, MenuItemType::getCodec);

    Integer slot();

    Action action();

    ItemStack resolveItemStack(ServerPlayerEntity player);

    MenuItemType getType();
}
