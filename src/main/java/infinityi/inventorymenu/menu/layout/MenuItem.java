package infinityi.inventorymenu.menu.layout;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.action.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface MenuItem {
    Codec<MenuItem> CODEC = StringRepresentable.fromEnum(MenuItemType::values)
            .dispatch(MenuItem::getType, MenuItemType::getCodec);

    List<Action> actions();

    ItemStack resolveItemStack(ServerPlayer player);

    MenuItemType getType();

}
