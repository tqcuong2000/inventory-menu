package infinityi.inventorymenu.menulayout.itemtype;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.itemaction.Action;
import infinityi.inventorymenu.itemaction.actions.NoAction;
import infinityi.inventorymenu.menulayout.layout.MenuItem;
import infinityi.inventorymenu.menulayout.layout.MenuItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public record StaticItem(ItemStack item, List<Action> actions) implements MenuItem {
    public static final MapCodec<StaticItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(StaticItem::item),
            Action.LIST_CODEC.optionalFieldOf("action", Action.EMPTY_LIST).forGetter(MenuItem::actions)
    ).apply(instance, StaticItem::new));

    @Override
    public ItemStack resolveItemStack(ServerPlayerEntity player) {
        return this.item.copy();
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.ITEM;
    }
}
