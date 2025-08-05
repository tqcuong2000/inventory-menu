package infinityi.inventoryMenu.MenuLayout.ItemType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.ItemAction.Action;
import infinityi.inventoryMenu.ItemAction.Actions.NoAction;
import infinityi.inventoryMenu.MenuLayout.layout.MenuItem;
import infinityi.inventoryMenu.MenuLayout.layout.MenuItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public record StaticItem(int slot, ItemStack item, Action action) implements MenuItem {
    public static final MapCodec<StaticItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(0, 26).fieldOf("slot").forGetter(StaticItem::slot),
            ItemStack.CODEC.fieldOf("item").forGetter(StaticItem::item),
            Action.CODEC.optionalFieldOf("action", new NoAction()).forGetter(StaticItem::action)
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
