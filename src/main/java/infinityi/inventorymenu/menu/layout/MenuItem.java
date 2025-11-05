package infinityi.inventorymenu.menu.layout;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.action.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;

import java.util.List;

public interface MenuItem {
    Codec<MenuItem> CODEC = StringIdentifiable.createCodec(MenuItemType::values)
            .dispatch(MenuItem::getType, MenuItemType::getCodec);

    List<Action> actions();

    ItemStack resolveItemStack(ServerPlayerEntity player);

    MenuItemType getType();

}
