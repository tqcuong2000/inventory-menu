package infinityi.inventorymenu.menulayout.layout;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.itemaction.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;

public interface MenuItem {
    Codec<MenuItem> CODEC = StringIdentifiable.createCodec(MenuItemType::values)
            .dispatch(MenuItem::getType, MenuItemType::getCodec);

    Action action();

    ItemStack resolveItemStack(ServerPlayerEntity player);

    MenuItemType getType();

}
