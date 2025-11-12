package infinityi.inventorymenu.menu.itemtype;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.menu.layout.MenuItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public record StandardItem(ItemStack item, List<Action> actions) implements MenuItem {
    public static final MapCodec<StandardItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(StandardItem::item),
            Action.LIST_CODEC.optionalFieldOf("action", Action.EMPTY_LIST).forGetter(MenuItem::actions)
    ).apply(instance, StandardItem::new));

    @Override
    public ItemStack resolveItemStack(ServerPlayerEntity player) {
        return this.item.copy();
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.ITEM;
    }
}
