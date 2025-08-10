package infinityi.inventorymenu.menulayout.itemtype;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.itemaction.Action;
import infinityi.inventorymenu.itemaction.actions.NoAction;
import infinityi.inventorymenu.menulayout.layout.MenuItem;
import infinityi.inventorymenu.menulayout.layout.MenuItemType;
import infinityi.inventorymenu.menulayout.layout.SlotPair;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public record StaticItem(SlotPair slotPair, ItemStack item, Action action) implements MenuItem {
    public static final MapCodec<StaticItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SlotPair.LIST_CODEC
                    .xmap(l -> new SlotPair(l.getFirst(), l.getLast()), sp -> List.of(sp.row(), sp.column()))
                    .forGetter(StaticItem::slotPair),
            ItemStack.CODEC.fieldOf("item").forGetter(StaticItem::item),
            Action.CODEC.optionalFieldOf("action", new NoAction()).forGetter(StaticItem::action)
    ).apply(instance, StaticItem::new));

    @Override
    public Integer slot() {
        return slotPair.resolveSlot();
    }

    @Override
    public ItemStack resolveItemStack(ServerPlayerEntity player) {
        return this.item.copy();
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.ITEM;
    }
}
